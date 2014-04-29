package com.example.qiumishequouzhan.Utils;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * android 完全退出应用系统
 *
 * android 退出应用程序会调用android.os.Process.killProcess(android.os.Process.myPid())
 * 或是System.exit(0)，这只是针对第一个Activity(也就是入口的Activity)时生效。如果有A,B,C
 * 三个Activity,而想在B 或C 中Activity 退出，调用上面的方法，往往会销毁当前的Activity 返回上
 * 一个Activity。当然也可以逐个返回上一个Activity，直到跳转到入口的Activity，最后退出应用程
 * 序。但这样比较麻烦，而且逐个返回的体验并不友好。
 *  网上比较流行的方法是定义栈，写一个ExitApplication 类，利用单例模式管理Activity，在每个在
 *  Activity 的onCreate()方法中调用ExitApplication.getInstance().addActivity(this)方法,在退
 * 出时调用ExitApplication.getInstance().exit()方法，就可以完全退出应用程序了。
 *
 * http://my.eoe.cn/niunaixiaoshu/archive/1227.html
 */
public class ExitApplication extends Application {
    private List<Activity> activities = new ArrayList<Activity>();
    private static ExitApplication instance;

    private ExitApplication(){

    }

    // 单例模式获取唯一的实例
    public static ExitApplication getInstance(){
        if(null == instance){
            instance = new ExitApplication();
        }
        return instance;
    }

    //添加Activity 到容器中
    public void addActivity(Activity activity){
        activities.add(activity);
    }

    //遍历所有Activity 并finish
    public  void exit(){
        for(Activity activity: activities){
            activity.finish();
        }
        System.exit(0); // 0:正常退出
    }
}
