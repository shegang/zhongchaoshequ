package com.example.qiumishequouzhan.webviewpage;

import android.app.Activity;
import android.os.Bundle;
import com.example.qiumishequouzhan.Utils.ExitApplication;


/**
 * Created by Administrator on 14-2-16.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApplication.getInstance().addActivity(this);
    }
}
