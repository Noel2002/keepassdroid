package com.keepassdroid.sync.utilities;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.keepassdroid.Database;
import com.keepassdroid.database.PwDatabase;
import com.keepassdroid.database.PwEntry;
import com.keepassdroid.database.PwEntryV3;
import com.keepassdroid.database.PwEntryV4;
import com.keepassdroid.database.PwGroup;
import com.keepassdroid.database.PwGroupV3;
import com.keepassdroid.database.PwGroupV4;
import com.keepassdroid.database.edit.AddEntry;
import com.keepassdroid.database.edit.OnFinish;
import com.keepassdroid.database.edit.RunnableOnFinish;
import com.keepassdroid.database.edit.UpdateEntry;

import com.keepassdroid.sync.models.CredentialChange;

import java.util.ArrayList;
import java.util.List;

public class Merger {
    private Database database;
    private PwGroup pwGroup;
    private Activity context;

    private PwDatabase pwDatabase;

    private static Merger instance;

    public static Merger getInstance() throws Exception {
        if(instance==null){
            throw new Exception("Merger is not initialized");
        }
        return Merger.instance;
    }

    public static void init(Database database, Activity context){
        Merger.instance = new Merger(database, context);
    }
    public Merger(Database database, Activity context) {
        this.database = database;
        this.context = context;
        this.pwDatabase = database.pm;
        this.pwGroup = this.pwDatabase.rootGroup;
    }


    private void merge(PwEntry current, CredentialEntry incoming){
        if(incoming.lastModified < UnixTimeConverter.DateTimeToUnixSeconds(current.getLastModificationTime())){
            return;
        }
        PwEntry newEntry = current.clone(true);
        newEntry.setTitle(incoming.title, this.pwDatabase);
        newEntry.setUrl(incoming.url, this.pwDatabase);
        newEntry.setPassword(incoming.password, this.pwDatabase);
        newEntry.setUsername(incoming.username, this.pwDatabase);


        OnFinish onUpdateFinish = new AfterSave();
        RunnableOnFinish task = new UpdateEntry(this.context, this.database, current, newEntry, onUpdateFinish);

        task.run();
    }

    public void addEntry(CredentialEntry incoming){
        PwEntry entry;
        if(pwGroup instanceof PwGroupV4){
            entry = new PwEntryV4((PwGroupV4) this.pwGroup, true, true);
        }
        else{
            entry = new PwEntryV3((PwGroupV3) this.pwGroup, true, true);
        }

        OnFinish onAddFinish = new AfterSave();

        entry.setTitle(incoming.title, this.pwDatabase);
        entry.setUsername(incoming.username, this.pwDatabase);
        entry.setPassword(incoming.password, this.pwDatabase);
        entry.setUrl(incoming.url, this.pwDatabase);

        RunnableOnFinish task= AddEntry.getInstance(this.context, this.database, entry, onAddFinish);

        task.run();
    }

    private PwEntry findMatch(CredentialEntry incoming){
        for(PwEntry entry: this.pwGroup.childEntries){
            if(entry.getUsername().equals(incoming.username) &&
                    (entry.getTitle().equals(incoming.title) || entry.getUrl().equals(incoming.url))
            ){
                return entry;
            }
        }
        return null;
    }

    public List<CredentialChange> merge(List<CredentialEntry> receivedEntries){
        List<CredentialChange> changes = new ArrayList<>();
        for(CredentialEntry incoming: receivedEntries){
            PwEntry entry = this.findMatch(incoming);
            if(entry == null){
                addEntry(incoming);
                changes.add(new CredentialChange(incoming.title, incoming.username, "created"));
            }
            else{
                this.merge(entry, incoming);
                changes.add(new CredentialChange(incoming.title, incoming.username, "modified"));
            }
        }
        return changes;
    }

    public List<CredentialEntry> getModifiedEntries(){
        List<CredentialEntry> entries = new ArrayList<>();
        for (PwEntry entry: this.pwGroup.childEntries) {
            entries.add(new CredentialEntry(entry));
        }
        return entries;
    }

    private final class AfterSave extends OnFinish {

        public AfterSave() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        public void run() {
            if (mSuccess) {
                System.out.println("Entry updated");
            } else {
                System.err.println("Entry failed to update");
            }
        }
    }

}
