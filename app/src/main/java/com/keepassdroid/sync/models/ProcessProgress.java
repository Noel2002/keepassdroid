package com.keepassdroid.sync.models;

public class ProcessProgress {
    private String process;
    private String status;

    public ProcessProgress(String process, String status) {
        this.process = process;
        this.status = status;
    }

    public String getProcess() {
        return process;
    }

    public String getStatus() {
        return status;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
