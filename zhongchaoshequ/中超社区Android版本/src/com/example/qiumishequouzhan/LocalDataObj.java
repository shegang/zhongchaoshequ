package com.example.qiumishequouzhan;

import android.content.SharedPreferences;
import com.example.qiumishequouzhan.R;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-27
 * Time: 上午11:06
 * To change this template use File | Settings | File Templates.
 */
public class LocalDataObj {
    public static String GetUserLocalData(String sKeyName)
    {
        sKeyName = sKeyName.toUpperCase();
        SharedPreferences settings = ExampleApplication.GetInstance().getSharedPreferences(ExampleApplication.GetInstance().getString(R.string.userlocaldata), 0);
        return settings.getString(sKeyName, "");
    }
    public static void SetUserLocalData(String sKeyName,String Value)
    {
        if(sKeyName.equalsIgnoreCase("UserID"))
        {
            ExampleApplication.GetInstance().SetJpushAlias(Value);
        }
        sKeyName = sKeyName.toUpperCase();
        SharedPreferences settings = ExampleApplication.GetInstance().getSharedPreferences(ExampleApplication.GetInstance().getString(R.string.userlocaldata), 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(sKeyName, Value);
        editor.commit();
    }
}
