package com.keepassdroid.sync.models;

public class CredentialChange {
    private String title;
    private String username;
    private String changeType;

    public CredentialChange(String title, String username, String changeType) {
        this.title = title;
        this.username = username;
        this.changeType = changeType;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
}
