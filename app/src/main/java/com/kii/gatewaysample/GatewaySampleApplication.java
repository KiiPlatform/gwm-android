package com.kii.gatewaysample;

import android.app.Application;

public class GatewaySampleApplication extends Application {
    private static GatewaySampleApplication INSTANCE = null;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
    public static GatewaySampleApplication getInstance() {
        return INSTANCE;
    }
}
