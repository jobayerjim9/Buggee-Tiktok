package com.systematics.buggee.SimpleClasses;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;


/**
 * Created by AQEEL on 3/18/2019.
 */

public class Buggee extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);

    }

}