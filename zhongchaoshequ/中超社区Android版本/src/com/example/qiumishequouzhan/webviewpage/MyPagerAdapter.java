package com.example.qiumishequouzhan.webviewpage;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.example.qiumishequouzhan.MainView.MainActivity;

import java.util.List;

/**
 * 欢迎界面的PagerAdapter
 *
 * 这里我们要绑定的每一个item就是一个引导界面，我们用一个list来保存。
 * 通过继承PagerAdapter，并实现几个我写注释的方法即可。
 * 布局界面比较简单，加入ViewPager组件，以及底部的引导小点：
 */
public class MyPagerAdapter extends PagerAdapter {
    //界面列表
    private List<View> views;

    // 构造方法
    public MyPagerAdapter(List<View> views) {
        this.views = views;
    }

    //获得当前界面数
    @Override
    public int getCount() {
        if (views != null)  {
            return views.size();
        }
        return 0;
    }

    @Override
    public void startUpdate(View view) {

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);

    }

    //初始化arg1位置的界面
    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(views.get(arg1), 0);
        if(arg1 == 3){
          //  Intent intent = new Intent(GuidPage.this, MainActivity.class);
        }
        return views.get(arg1);
    }

    //销毁arg1位置的界面
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1));
    }

    @Override
    public void finishUpdate(View view) {

    }

    //判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {

    }
}
