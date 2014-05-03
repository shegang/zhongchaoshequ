package com.example.qiumishequouzhan.webviewpage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.example.qiumishequouzhan.LocalDataObj;
import com.example.qiumishequouzhan.MainView.MainActivity;
import com.example.qiumishequouzhan.R;
import com.example.qiumishequouzhan.Utils.Splash;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Manc on 14-4-15.
 */
public class WelcomPage extends BaseActivity {

    private static final String FILENMAE = "zhongchaoshequ";
    private String firstFlag = ""; // 程序是否是第一次运行,默认是第一运行
    private Intent intent;
    public static WelcomPage welcomPage;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //去掉标题栏（设置Activity的标题，必须在setContentView之前）
//        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //去掉信息栏
//        super.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.setContentView(R.layout.activity_welcome);




        imageView = (ImageView) findViewById(R.id.img_qidong);
        Splash.ThreadDownLoadSplash();
        Drawable splash = Splash.GetSplash();
        if (splash == null) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.qidong));
        } else {
            if(firstFlag.equals("")){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.qidong));
            }else{
                imageView.setImageDrawable(splash);
            }
        }
        // 存储数据程序（程序是否是第一次运行）
        // FILENMAE 文件名称，一xml形式，存放在/data/data/<程序包名>/xxxx.xml
        // Activity.MODE_PRIVATE：文件保存模式，即访问权限


        SharedPreferences share = super.getSharedPreferences(FILENMAE, Activity.MODE_PRIVATE);
        firstFlag = LocalDataObj.GetUserLocalData("firstFlag");//share.getBoolean("firstFlag", true); // 默认false
        if (firstFlag.equals("")) { // 第一次运行
            intent = new Intent(this, GuidPage.class);
            LocalDataObj.SetUserLocalData("firstFlag","1");
        } else {

            intent = new Intent(this, MainActivity.class);

        }


        Timer time = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                startActivity(intent);
                WelcomPage.this.finish();
            }
        };
        time.schedule(task, 2000);// 2秒进行跳转

    }



}
