package com.example.qiumishequouzhan.webviewpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageButton;
import com.example.qiumishequouzhan.MainView.MainActivity;
import com.example.qiumishequouzhan.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Manc on 14-4-15.
 */
public class GuidPage extends BaseActivity {
    private ViewPager mViewPager;
    private ImageButton imageButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置没有Activity的title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去掉信息栏：
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_whatsnew);


        mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);

        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.whats_news_gallery_one, null);
        View view2 = mLi.inflate(R.layout.whats_news_gallery_two, null);
        View view3 = mLi.inflate(R.layout.whats_news_gallery_three, null);
        View view4 = mLi.inflate(R.layout.whats_news_gallery_four, null);

        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

        final  Intent intent = new Intent(GuidPage.this, MainActivity.class);
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(views);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( mViewPager.getChildAt(3)==v){
                   int i = mViewPager.getCurrentItem();
                   startActivity(intent);
                   GuidPage.this.finish();
               };
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i==views.size()-1) {
                    Timer time = new Timer();

                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {

                            startActivity(intent);
                            GuidPage.this.finish();
                        }
                    };
                    time.schedule(task, 1000);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

}
