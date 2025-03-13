package com.keepassdroid.sync;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.android.keepass.R;
import com.keepassdroid.Database;
import com.keepassdroid.EntryEditActivity;
import com.keepassdroid.app.App;
import com.keepassdroid.database.PwEntry;
import com.keepassdroid.database.PwGroup;
import com.keepassdroid.database.edit.OnFinish;
import com.keepassdroid.database.edit.RunnableOnFinish;
import com.keepassdroid.database.edit.UpdateEntry;

import java.util.List;
import java.util.Random;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_sync);

        Database db = App.getDB();
        PwGroup root = db.pm.rootGroup;
        List<PwEntry> entries = root.childEntries;

        Random random = new Random();

        for (PwEntry entry : entries) {
            // Generate a random number between 0 and 999
            int randomNumber = random.nextInt(1000);

            // Get the current password
            String currentPassword = new String(entry.getPassword());

            // Append the random number to the password
            String newPassword = currentPassword + randomNumber;

            // Create a clone of the entry with the updated password
            PwEntry newEntry = entry.clone(true);
            newEntry.setPassword(newPassword, db.pm);

            // Update the entry in the database
            OnFinish onFinish = TestActivity.this.new AfterSave(new Handler());

            RunnableOnFinish task = new UpdateEntry(this, db, entry, newEntry, onFinish);

            // Run the update task
            task.run();
        }
    }

    private final class AfterSave extends OnFinish {

        public AfterSave(Handler handler) {
            super(handler);
        }

        @Override
        public void run() {
            if ( mSuccess ) {
                finish();
            } else {
                displayMessage(TestActivity.this);
            }
        }

    }
}
