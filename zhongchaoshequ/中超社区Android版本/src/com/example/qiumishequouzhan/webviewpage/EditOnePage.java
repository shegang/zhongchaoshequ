package com.example.qiumishequouzhan.webviewpage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.example.qiumishequouzhan.R;
import com.example.qiumishequouzhan.ExampleApplication;
import com.example.qiumishequouzhan.LocalDataObj;
import com.example.qiumishequouzhan.MainView.MainActivity;

import com.example.qiumishequouzhan.Utils.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Manc on 14-4-14.
 */
public class EditOnePage extends ProgressFragment {    //R.id.one_page_webView
    private View mContentView;
    private Handler mHandler;
    private static WebView mWebview;
    private static PullToRefreshWebView p_PushInstance;
    private static String msUrl;
    public TextView titleView;
    public TextView ButtonText;
    public ImageButton backbutton;
    public ImageButton Rightbutton;
    public TextView comment_textview;
    public EditText comment_text;
    public ImageButton bt_send;
    public RelativeLayout comment_layout;
    public boolean buttonstate;
    private String comment;
    private int EvalID;
    public long time1, time2;
    public String shareCount;//分享的内容
    public String imgPath;
    public String pathURL;


    public static void SetWebViewUrl(String sUrl) {
        msUrl = sUrl;
    }

    public static EditOnePage newInstance() {
        EditOnePage fragment = new EditOnePage();
        return fragment;
    }

    public static EditOnePage obj;

    public static EditOnePage GetInstance() {
        return obj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.editonepage, null);
        return inflater.inflate(R.layout.fragment_custom_progress, container, false);
    }

    public void alertTips(String mes) {
        comment_textview.setText("发布评论");
        comment_text.setText("");//清空
        AlertDialog.Builder builder = new AlertDialog.Builder(getContentView().getContext());
        builder.setTitle("中超社区");
        builder.setPositiveButton("确定", null);
        builder.setMessage(mes);
        builder.show();
    }

    View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Url;
            String webview_url = mWebview.getUrl();
            switch (v.getId()) {
                case R.id.comment_text:

                    comment_text.setFocusable(true);
                    comment_text.requestFocus();
                    break;
                case R.id.comment_textview:
                    comment_textview.setText("发布评论");
                    EvalID = 0;
                    break;
                case R.id.back_button:

                    Bundle bundle = new Bundle();
                    bundle.putInt("ChangeState", 3);
                    getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));
                    getActivity().finish();
                    break;
                case R.id.bt_send:

                    final String uid = LocalDataObj.GetUserLocalData("UserID");
                    if (uid.equalsIgnoreCase("100") == true) {//tips   游客用户无法评论
                        Toast.makeText(getContentView().getContext(), "您还未登录暂时无法评论", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContentView().getContext(), MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, ExampleApplication.GetInstance().getString(R.string.denglu_view));
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

//                         startActivity(intent);
                        startActivityForResult(intent, 0);
                        return;
                    }
                    final String commenttext = comment_text.getText().toString();

                    if (commenttext.equals("") || null == commenttext) {
                        alertTips("内容不能为空");
                        return;
                    }

                    String s1[] = webview_url.split("\\?");
                    s1 = s1[1].split("\\&");
                    s1 = s1[0].split("\\=");
                    final String sNewsID = s1[1];
                    time1 = System.currentTimeMillis();//记录点击开始发送的时间
                    if (time1 - time2 < 20000 && time2 > 0) {
                        Toast.makeText(getContentView().getContext(), "不能连续发送评论", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                String webview_url = mWebview.getUrl();
                                String Url = getString(R.string.serverurl) + "/InsertEvaluate";
                                byte[] data = null;
                                time2 = System.currentTimeMillis();
                                if (webview_url.contains("EvaluatePage")) {//这个是竞猜评论管理中的发送
                                    data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"," +
                                            "\"ID\":" + sNewsID + ", \"evalContent\":'" + commenttext + "'," +
                                            " \"parentID\":" + EvalID + ",\"EvalType\":\"C\"}");
                                } else if (webview_url.contains("SelectNews")) {
                                    data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"," +
                                            "\"ID\":" + sNewsID + ", \"evalContent\":'" + commenttext + "'," +
                                            " \"parentID\":" + EvalID + ",\"EvalType\":\"N\"}");
                                }

                                String Result = FileUtils.Bytes2String(data);
                                JSONObject Json = JsonUtils.Str2Json(Result);
                                try {
                                    Json = Json.getJSONObject("d");
                                } catch (JSONException e) {
                                    LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                                }
                                Message MSG = new Message();
                                MSG.arg1 = 3;
                                updateHandler.sendMessage(MSG);

                            }
                        }.start();

                    }
                    break;
                case R.id.right_button:
                    if (webview_url.contains("AccountManager"))//账号管理有两种按钮状态
                    {
                        if (buttonstate)   //开启编辑状态
                        {
                            Url = "javascript:MoreDelete()";
                            mWebview.loadUrl(Url);
                            buttonstate = false;
                            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.zhanghaoeditwancheng));
                        } else   //编辑完成状态
                        {
                            Url = "javascript:FinishAccount()";
                            mWebview.loadUrl(Url);
                            buttonstate = true;
                            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.zhanghaoedit));
                        }

                    } else if (webview_url.contains("SelectNews")) {
                        Url = "javascript:ShareNews()";
                        mWebview.loadUrl(Url);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(mContentView);
        setEmptyText("空的啊");

        buttonstate = true;

        titleView = (TextView) mContentView.findViewById(R.id.titlename);
        titleView.setText(R.string.maintitle);

        ButtonText = (TextView) mContentView.findViewById(R.id.buttontextView);

        backbutton = (ImageButton) mContentView.findViewById(R.id.back_button);
        backbutton.setOnClickListener(button_listener);
        backbutton.setImageDrawable(getResources().getDrawable(R.drawable.backico_on));

        comment_textview = (TextView) mContentView.findViewById(R.id.comment_textview);
        comment_textview.setOnClickListener(button_listener);

        comment_text = (EditText) mContentView.findViewById(R.id.comment_text);

        comment_layout = (RelativeLayout) mContentView.findViewById(R.id.comment_layout);


        Rightbutton = (ImageButton) mContentView.findViewById(R.id.right_button);
        Rightbutton.setOnClickListener(button_listener);
        Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.tittlebtn));

        bt_send = (ImageButton) mContentView.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(button_listener);

        Rightbutton.setVisibility(View.GONE);
        ButtonText.setVisibility(View.GONE);

        //SelectNews 分享     shareicon
        //CashPrizes 兑换记录
        //Guess 财富榜
        //GuessWinLost 评论
        //ChoosePlace新增  x
        //   NewPlace 确定
        //ModiftyAddress 确定
        //AccountManager 账号管理zhanghaoedit  or 完成zhanghaoeditwancheng
        //Message 管理  or  完成


        p_PushInstance = (PullToRefreshWebView) mContentView.findViewById(R.id.one_page_webView);

        p_PushInstance.setGravity(Gravity.BOTTOM);
        mWebview = p_PushInstance.getRefreshableView();
        p_PushInstance.setMode(PullToRefreshBase.Mode.BOTH);

        p_PushInstance.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        p_PushInstance.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
        p_PushInstance.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
        p_PushInstance.getLoadingLayoutProxy(true, true).setReleaseLabel("释放开始加载");
        p_PushInstance.getLoadingLayoutProxy(true, true).setRefreshingLabel("正在加载");

        p_PushInstance.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<WebView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<WebView> refreshView) {
                SetRefreshTitle(false);

//                String Url = "javascript:CallBackData(0,'PullDownToRefresh',true,'')";
//                p_PushInstance.onRefreshComplete();
                mWebview.reload();
                p_PushInstance.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<WebView> refreshView) {
                SetRefreshTitle(true);

                String Url = "javascript:OnLoad()";
                mWebview.loadUrl(Url);
                p_PushInstance.onRefreshComplete();
            }

            public void SetRefreshTitle(Boolean mIsUp) {
                //获取刷新时间，设置刷新时间格式
                String str = DateUtils.formatDateTime(MainActivity.GetInstance(), System.currentTimeMillis(), (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE));
                // 设置刷新文本说明（刷新过程中）
                if (mIsUp) {
                    p_PushInstance.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("最后加载时间:" + str);
                } else {
                    p_PushInstance.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("最后更新时间:" + str);
                }
            }
        });
        mWebview.setWebChromeClient(new UserWebClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
                Log.d("jing_log_debug", String.valueOf(progress));
                if (progress >= 100) {
                    setContentShown(true);
                }
            }
        });

        mWebview.requestFocus();
        WebSettings mWebSetting = mWebview.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        //自适应屏幕显示
        mWebSetting.setUseWideViewPort(true);
        mWebSetting.setLoadWithOverviewMode(true);

        mWebSetting.setDefaultTextEncodingName("UTF-8");
        mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSetting.setAppCacheMaxSize(1024 * 1024 * 8);

        mWebSetting.setAllowFileAccess(true);
        mWebSetting.setAppCacheEnabled(true);

        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebSetting.setSupportZoom(false);
        mWebSetting.setBuiltInZoomControls(false);
        mWebSetting.setGeolocationEnabled(true);
        mWebSetting.setDatabaseEnabled(true);

        mWebSetting.setDomStorageEnabled(true);
        mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebview.addJavascriptInterface(new JavaScriptInterface(), "javatojs");

        mWebview.setWebViewClient(new WebViewClient() {
            public ProgressDialog progressBar;
            public AlertDialog alertDialog;


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (progressBar == null) {
                    progressBar = ProgressDialog.show(MainFragment.GetInstance(), null, "正在加载，请稍后…");
                }
                if (!progressBar.isShowing()) {
                    progressBar.show();
                }
//        progressBar = ProgressDialog.show(MainActivity.GetInstance(), null, "正在加载，请稍后…");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
//            MainActivity.GetPushInstance().onRefreshComplete();
                }
            }
        });
        mWebview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                MainFragment.GetInstance().startActivity(intent);
            }
        });
        titleView.setText(R.string.title1);
        if (msUrl.contains("PageUserInfo")) {
            titleView.setText(R.string.title2);
        }
        if (msUrl.contains("SelectNews")) {
            titleView.setText(R.string.title1); //shareicon
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.shareicon));
            Rightbutton.setVisibility(View.VISIBLE);
            comment_layout.setVisibility(View.VISIBLE);
        }
        if (msUrl.contains("EvaluatePage")) {
            titleView.setText(R.string.title1); //shareicon
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.shareicon));
            Rightbutton.setVisibility(View.GONE);
            comment_layout.setVisibility(View.VISIBLE);
        }
        if (msUrl.contains("Guess")) {
            titleView.setText(R.string.title3);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string2);
        }
        if (msUrl.contains("GuessWinLost")) {
            titleView.setText(R.string.title3);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string3);
        }
        if (msUrl.contains("CampList")) {
            titleView.setText(R.string.title4);
        }
        if (msUrl.contains("TopScrollList")) {
            titleView.setText(R.string.title4);
        }
        if (msUrl.contains("CampScheduleList")) {
            titleView.setText(R.string.title4);
        }
        if (msUrl.contains("Shake")) {
            titleView.setText(R.string.title5);
        }
        if (msUrl.contains("CashPrizes")) {
            titleView.setText(R.string.title6);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string1);

        }
        if (msUrl.contains("Details")) {
            titleView.setText(R.string.title7);
        }
        if (msUrl.contains("PlayerInformation")) {
            titleView.setText(R.string.title8);
        }
        if (msUrl.contains("Cash")) {
            titleView.setText(R.string.title9);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("DeliveryTime")) {
            titleView.setText(R.string.title10);
        }
        if (msUrl.contains("ChoosePlace")) {
            titleView.setText(R.string.title11);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string4);
        }
        if (msUrl.contains("NewPlace")) {
            titleView.setText(R.string.title12);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string5);
        }
        if (msUrl.contains("ModiftyAddress")) {
            titleView.setText(R.string.title13);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string5);
        }
        if (msUrl.contains("PageUserInfoData")) {
            titleView.setText(R.string.title14);
        }
        if (msUrl.contains("Evaluate")) {
            titleView.setText(R.string.title15);
        }
        if (msUrl.contains("ReplyEvaluate")) {
            titleView.setText(R.string.title15);
        }
        if (msUrl.contains("UpdatePwassword")) {
            titleView.setText(R.string.title16);
        }
        if (msUrl.contains("Login")) {
            titleView.setText(R.string.title17);
        }
        if (msUrl.contains("Register")) {
            titleView.setText(R.string.title18);
        }
        if (msUrl.contains("Record")) {
            titleView.setText(R.string.title19);
        }
        if (msUrl.contains("SendEmail")) {
            titleView.setText(R.string.title27);
        }
        if (msUrl.contains("AccountManager")) {
            titleView.setText(R.string.title20);
            Rightbutton.setVisibility(View.VISIBLE);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.zhanghaoedit));
        }
        if (msUrl.contains("MyCard")) {
            titleView.setText(R.string.title21);
        }
        if (msUrl.contains("Order")) {
            titleView.setText(R.string.title22);
        }
        if (msUrl.contains("Message")) {
            titleView.setText(R.string.title23);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            ButtonText.setText(R.string.string6);
        }
        if (msUrl.contains("AboutUs")) {
            titleView.setText(R.string.title24);
        }
        if (msUrl.contains("SelectSoftware")) {
            titleView.setText(R.string.title25);
        }
        if (msUrl.contains("GoldRankPage")) {
            titleView.setText(R.string.title26);
        }
        mWebview.loadUrl(msUrl);
        obtainData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1://登陆wancheng
//                    Intent intent=new Intent();
//                    intent.setClass(MainFragment.GetInstance(), MainActivity.class);
//                    startActivity(intent);


                    getActivity().finish();
                    break;
                case 2:    //通知页面停止刷新
                    if (p_PushInstance.isRefreshing())
                        p_PushInstance.onRefreshComplete();
                    break;
                case 3://页面重新刷新
                    comment_text.setText("");//清空
                    comment_textview.setText("发布评论");
                    EvalID = 0;
                    // comment_text.setFocusable(false);//失去焦点
                    mWebview.reload();
                    InputMethodManager m = (InputMethodManager) mContentView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
                case 4://点击回复之后
                    comment_textview.setText(comment);
                    break;
                case 5://添加分享平台和内容
                    UMengUtils.InitUMengConfig(mContentView.getContext());//添加分享平台
                    UMengUtils.ShareContent(shareCount);//添加分享能容
                    //share();
                    break;
                case 6://新闻分享
//                    shareCount = objectParams.getString("Title");
//                    imgPath = objectParams.getString("NewsImg");
//                    pathURL = objectParams.getString("Url");
                    //  UMengUtils.InitUMengConfig(mContentView.getContext());//添加分享平台
                    UMengUtils.InitUMengConfig(mContentView.getContext());
                    UMengUtils.ShareContent(shareCount, ExampleApplication.GetInstance().getString(R.string.BaseIP) + imgPath, ExampleApplication.GetInstance().getString(R.string.BaseIP) + pathURL);//添加分享能容
                    share();
                    break;
            }
        }
    };

    public void share() {
        new Thread() {
            @Override
            public void run() {
                //你要执行的方法
                //执行完毕后给handler发送一个空消息
                String Url = getString(R.string.serverurl) + "/ShareContent";
                byte[] data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                        " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"}");
                String Result = FileUtils.Bytes2String(data);
                JSONObject Json = JsonUtils.Str2Json(Result);
                try {
                    Json = Json.getJSONObject("d");
                    Json = Json.getJSONObject("Data");
                } catch (JSONException e) {
                    LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                }
            }
        }.start();
    }

    private void obtainData() {
        // Show indeterminate progress
        setContentShown(false);
    }

    public class JavaScriptInterface {
        @JavascriptInterface
        public void RunAndroidFunction(String JsonParams) throws JSONException {
            Log.i("COMMAND", JsonParams);

            JSONObject Json = JsonUtils.Str2Json(JsonParams);
            if (Json == null) return;

            try {
                int Type = Json.getInt("Type");
                String Name = Json.getString("Name");
                String Params = Json.getString("Parms");
                JSONObject objectParams;

                switch (Type) {
                    case 1:
                        break;
                    case 0:
                        if (Name.equalsIgnoreCase("ShearContent")) {
                            objectParams = JsonUtils.Str2Json(Params);
                            if (objectParams.isNull("Content")) {
                                shareCount = objectParams.getString("Title");
                                imgPath = objectParams.getString("NewsImg");
                                pathURL = objectParams.getString("Url");
                                Message MSG = new Message();
                                MSG.arg1 = 6;
                                updateHandler.sendMessage(MSG);

                            } else {
                                shareCount = objectParams.getString("Content");
                                Message MSG = new Message();
                                MSG.arg1 = 5;
                                updateHandler.sendMessage(MSG);

                            }

                            break;
                            // ShareContent
                        }

                        if (Name.equalsIgnoreCase("ReplayEval") == true)//这个是点击回复接口
                        {
                            objectParams = JsonUtils.Str2Json(Params);
                            String UserNick = objectParams.getString("UserNick");
                            //parentID = objectParams.getInt("parentID");
                            comment = "回复" + UserNick + ":(点击取消回复)";
                            EvalID = objectParams.getInt("EvalID");
                            Message MSG = new Message();
                            MSG.arg1 = 4;
                            updateHandler.sendMessage(MSG);

                            break;
                        }
                        if (Name.equalsIgnoreCase("InsertEvaluate") == true) {
                            objectParams = JsonUtils.Str2Json(Params);
                            // Log(objectParams)
                            Log.d("CH", objectParams + "");
                            break;
                        }
                        if (Name.equalsIgnoreCase("DelEdit") == true)   //删除本地的缓存用户
                        {
                            objectParams = JsonUtils.Str2Json(Params);
                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                            JSONArray userArray = JsonUtils.Str2JsonArray(jsonStr);
                            String nameList = "";
                            JSONObject user = null;
                            JSONArray userNewArray = new JSONArray();
                            for (int i = 0; i < userArray.length(); i++) {
                                user = (JSONObject) userArray.get(i);
                                String username = objectParams.getString("UserNick");
                                if (username.equalsIgnoreCase(user.getString("UserNick"))) {
                                    continue;
                                }
                                userNewArray.put(user);
                            }
                            if (userNewArray.length() != 0) {
                                String userJsonStr = userNewArray.toString();
                                LocalDataObj.SetUserLocalData("LocalUserJson", userJsonStr);
                            }
                        }
                        if (Name.equalsIgnoreCase("OnLoadFinish") == true) {
                            Message MSG = new Message();
                            MSG.arg1 = 2;
                            updateHandler.sendMessage(MSG);
                            break;
                        }
                        if (Name.equalsIgnoreCase("JumpPage") == true) {
                            objectParams = JsonUtils.Str2Json(Params);
                            String isSelf = objectParams.getString("IsSelf");
                            if (isSelf.equalsIgnoreCase("true") == true) {
                                String url;
                                if (objectParams.getString("Parms").equalsIgnoreCase("?") == true) {
                                    url = ExampleApplication.GetInstance().getString(R.string.MainIP)
                                            + objectParams.getString("PageName")
                                            + ".aspx"
                                            + objectParams.getString("Parms")
                                            + "UserID="
                                            + LocalDataObj.GetUserLocalData("UserID")
                                            + "&Code="
                                            + LocalDataObj.GetUserLocalData("UserToken");
                                } else {
                                    url = ExampleApplication.GetInstance().getString(R.string.MainIP)
                                            + objectParams.getString("PageName")
                                            + ".aspx"
                                            + objectParams.getString("Parms")
                                            + "&UserID="
                                            + LocalDataObj.GetUserLocalData("UserID")
                                            + "&Code="
                                            + LocalDataObj.GetUserLocalData("UserToken");
                                }

                                mWebview.loadUrl(url);
                            } else {
                                String url = ExampleApplication.GetInstance().getString(R.string.MainIP)
                                        + objectParams.getString("PageName")
                                        + ".aspx"
                                        + objectParams.getString("Parms")
                                        + "&UserID="
                                        + LocalDataObj.GetUserLocalData("UserID")
                                        + "&Code="
                                        + LocalDataObj.GetUserLocalData("UserToken");

                                if (url != null) {


                                    Intent intent = new Intent(getActivity(), MainFragment.class);
                                    intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                    intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

//                                    startActivity(intent);
                                    startActivityForResult(intent, 0);
                                }
                            }
                            break;
                        }

                        break;

                }
            } catch (JSONException e) {
                LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
            }
        }
    }
}
