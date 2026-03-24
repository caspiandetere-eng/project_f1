package com.example.project_f1;

import android.app.Application;

public class F1App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StateManager.init(this);
    }
}
