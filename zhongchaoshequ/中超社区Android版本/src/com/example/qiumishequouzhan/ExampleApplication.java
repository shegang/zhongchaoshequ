package com.example.qiumishequouzhan;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import com.gotye.sdk.GotyeSDK;

import java.util.Set;

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
    public static void SetJpushAlias(String sKey)
    {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, sKey));
    }

    public static final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    public static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                   // JPushInterface.setAliasAndTags(ExampleApplication.GetInstance(), (String) msg.obj, null, mAliasCallback);
                    JPushInterface.setAlias(ExampleApplication.GetInstance(),(String) msg.obj,mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

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
