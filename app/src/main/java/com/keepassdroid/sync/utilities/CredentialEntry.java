package com.keepassdroid.sync.utilities;


import com.keepassdroid.database.PwEntry;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

public class CredentialEntry {
    public String username;
    public String password;
    public String url;
    public String title;
    public long lastModified;

    public CredentialEntry(String username, String password, String url, String title, Date lastModified) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.title = title;
        this.lastModified = UnixTimeConverter.DateTimeToUnixSeconds(lastModified);
    }

    public CredentialEntry(){}

    public CredentialEntry(PwEntry entry){
        this.username = entry.getUsername();
        this.password = entry.getPassword();
        this.url = entry.getUrl();
        this.title = entry.getTitle();
        this.lastModified = UnixTimeConverter.DateTimeToUnixSeconds(entry.getLastModificationTime());
    }

    static public CredentialEntry[] getSampleEntries(){
        Random random = new Random();
        int randomNumber = random.nextInt(101);
        CredentialEntry[] sampleEntries = {
                new CredentialEntry(
                        "testusername" + randomNumber,
                        "testpassword",
                        "testUrl",
                        "testTitle",
                        new Date()
                ),
                new CredentialEntry(
                        "testusername",
                        "testpassword" + randomNumber,
                        "testUrl",
                        "testTitle",
                        new Date()
                ),
                new CredentialEntry(
                        "testusername",
                        "testpassword",
                        "testUrl" + randomNumber,
                        "testTitle",
                        new Date()
                ),

        };
        return sampleEntries;
    }

}
