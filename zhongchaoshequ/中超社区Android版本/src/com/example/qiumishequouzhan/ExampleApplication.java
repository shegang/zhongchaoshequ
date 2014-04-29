package com.example.qiumishequouzhan;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import com.gotye.sdk.GotyeSDK;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-19
 * Time: 下午4:36
 * To change this template use File | Settings | File Templates.
 */
public class ExampleApplication extends Application {
    private static final String TAG = "ExampleApplication";
            private static  ExampleApplication app;
    public static ExampleApplication GetInstance()
    {
        return app;
    }
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        app = this;
        JPushInterface.setDebugMode(true); 	//??????????,????????????
        JPushInterface.init(this);     		// ????? JPush

        Bundle bundle = new Bundle();
        bundle.putString(GotyeSDK.PRO_APP_KEY, "524033b6-8cf9-4511-b44e-d897b2012fa8");
        GotyeSDK.getInstance().initSDK(this, bundle);


    }
}
