package com.keepassdroid.sync.views;

public class TabViewModel {
    private MainActivity context;

    private static TabViewModel instance;

    private TabViewModel(MainActivity context){
        this.context = context;
    }

    public static void init(MainActivity activity){
        instance = new TabViewModel(activity);
    }

    public static TabViewModel getInstance() throws Exception {
        if(instance == null) throw new Exception("TabViewModel is not initialized");
        return instance;
    }

    public void moveToProgressTab(){
        this.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.switchToProgressTab();
            }
        });
    }
}
