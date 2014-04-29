package com.example.qiumishequouzhan.MainView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NormalWebViewClient extends WebViewClient
{
    public ProgressDialog progressBar;
    public AlertDialog alertDialog;
    public NormalWebViewClient(MainActivity activity)
    {
        progressBar = ProgressDialog.show(activity, null, "正在加载，请稍后…");
//获得WebView组件

//        alertDialog = new AlertDialog.Builder(activity).create();
    }
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) 
	{
		view.loadUrl(url);
		return true;
	}
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (!progressBar.isShowing())
        {
            progressBar.show();
        }
//        progressBar = ProgressDialog.show(MainActivity.GetInstance(), null, "正在加载，请稍后…");
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        if(progressBar.isShowing()){
            progressBar.dismiss();
//            MainActivity.GetPushInstance().onRefreshComplete();
        }
    }
}
