package dev.xesam.android.cupboardtips;

import android.app.Application;

import dev.xesam.android.logtools.L;

/**
 * Created by xesamguo@gmail.com on 11/20/15.
 */
public class CupApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        L.enable(true);
    }
}
