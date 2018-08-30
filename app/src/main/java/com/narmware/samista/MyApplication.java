package com.narmware.samista;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by rohitsavant on 22/08/18.
 */

public class MyApplication extends Application {
    public static void mt(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
