package com.keepassdroid.sync.models;

import com.keepassdroid.sync.views.ProgressFragment;

public class ProcessProgressManager {
    private static ProcessProgressManager instance;
    private ProgressFragment context;

    private ProcessProgressManager(ProgressFragment context){
        this.context = context;
    }

    public static void init(ProgressFragment context){
        instance = new ProcessProgressManager(context);
    }

    public static ProcessProgressManager getInstance() throws Exception {
        if(instance == null) throw new Exception("Instance is not initialized");
        return instance;
    }

    public void addProcessProgress(String process, String status){
        ProcessProgress item = new ProcessProgress(process, status);
        context.requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.addProcessProgress(item);
            }
        });

    }

    public void addCredentialChange(String title, String username, String changeType){
        CredentialChange item = new CredentialChange(title, username, changeType);
        context.requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.addCredentialChange(item);
            }
        });

    }

    public void addCredentialChange(CredentialChange change){
        context.requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.addCredentialChange(change);
            }
        });

    }

    public void setStateText(String state){
        context.requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.setStateText(state);
            }
        });

    }
}
