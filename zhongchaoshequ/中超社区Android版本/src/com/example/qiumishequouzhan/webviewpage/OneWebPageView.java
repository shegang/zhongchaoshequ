package com.example.qiumishequouzhan.webviewpage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.example.qiumishequouzhan.ExampleApplication;
import com.example.qiumishequouzhan.LocalDataObj;
import com.example.qiumishequouzhan.MainView.MainActivity;
import com.example.qiumishequouzhan.R;
import com.example.qiumishequouzhan.Utils.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-19
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class OneWebPageView extends ProgressFragment {    //R.id.one_page_webView
    private View mContentView;
    private Handler mHandler;
    private WebView mWebview;
    private static PullToRefreshWebView p_PushInstance;
    private static String msUrl;
    private static String msTitle;
    public TextView titleView;
    public TextView ButtonText;
    public ImageButton backbutton;
    public ImageButton Rightbutton;
    public TextView comment_textview;
    public RelativeLayout comment_layout;
    public EditText comment_text;
    public ImageButton bt_send;
    public ImageButton bt_cancle;
    public boolean buttonstate;
    private String comment;
    private int EvalID;
    private String EvalType;
    private String sNewsID;
    private String UserNick;
    private final int ADD_SUCCESS = 0;
    private final int EDIT_SUCCESS = 1;
    private String RecUserId = "";
    private final int SEND_SUCCESS = 3;
    private int SchEvalCount = 0;
    public long time1, time2;
    public String shareCount;
    private boolean flage = true;
    public ProgressDialog progressBar;
    public int errorCode;

    public void SetWebViewUrl(String sUrl) {
        msUrl = sUrl;
    }

    public static void SetWebViewTitle(String sTitle) {
        msTitle = sTitle;
    }


    public static OneWebPageView newInstance() {
        OneWebPageView fragment = new OneWebPageView();
        obj = fragment;
        return fragment;
    }

    public static OneWebPageView obj;

    public static OneWebPageView GetInstance() {
        return obj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.onewebpage, null);
        return inflater.inflate(R.layout.fragment_custom_progress, container, false);
    }

    View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Url;
            String webview_url;
            String webview_Url = mWebview.getUrl();
            if (webview_Url.contains("Login")) {
                webview_url = MainActivity.usercountUrl;
                msUrl = MainActivity.usercountUrl;
                mWebview.loadUrl(webview_url);
            } else {
                webview_url = webview_Url;
            }

            switch (v.getId()) {

                case R.id.comment_text:
                    InputMethodManager m = (InputMethodManager) mContentView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    comment_text.setFocusable(true);
                    comment_text.requestFocus();
                    break;
                case R.id.bt_cancle:
                    if (comment_layout.getVisibility() == View.VISIBLE) {
                        comment_layout.setVisibility(View.GONE);
                    } else {
                        comment_layout.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.bt_send: {
                    final String uid = LocalDataObj.GetUserLocalData("UserID");
                    if (uid.equalsIgnoreCase("100") == true) {//tips   游客用户无法评论
                        //    Toast.makeText(getContentView().getContext(), "您还未登录暂时无法发送私信", Toast.LENGTH_SHORT).show();
                        alertTips("匿名账号无法发送");

                        return;
                    }
                    final String commenttext = comment_text.getText().toString();
                    if (commenttext.equals("") || null == commenttext) {
                        alertTips("内容不能为空");
                        return;
                    }
                    time1 = System.currentTimeMillis();//记录点击开始发送的时间
                    if (time1 - time2 < 20000 && time2 > 0) {
                        Toast.makeText(getContentView().getContext(), "不能连续发送评论", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                String webview_url = mWebview.getUrl();
                                String Url = "";
                                byte[] data = null;
                                time2 = System.currentTimeMillis();
                                if (webview_url.contains("Evaluate")) {
                                    Url = getString(R.string.serverurl) + "/InsertEvaluate";
                                    data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"," +
                                            "\"ID\":" + sNewsID + ", \"evalContent\":\"" + commenttext + "\"," +
                                            " \"parentID\":" + EvalID + ",\"EvalType\":\"" + EvalType + "\"}");
                                } else if (webview_url.contains("ReplyEvaluate")) {
                                    Url = getString(R.string.serverurl) + "/InsertEvaluate";
                                    data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"," +
                                            "\"ID\":" + sNewsID + ", \"evalContent\":\"" + commenttext + "\"," +
                                            " \"parentID\":" + EvalID + ",\"EvalType\":\"" + EvalType + "\"}");
                                } else {
                                    Url = getString(R.string.serverurl) + "/SendMailToUser";
                                    data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"," + " \"RecUserID\":" + RecUserId + "," +
                                            "\"Title\":\"" + commenttext + "\", \"Content\":\"" + commenttext + "\" }");
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
                                MSG.arg2 = SEND_SUCCESS;
                                updateHandler.sendMessage(MSG);
                            }
                        }.start();
                    }
                }
                break;
                case R.id.back_button:

                    Bundle bundle = new Bundle();
                    bundle.putInt("ChangeState", 3);
                    //  bundle.putInt("SchEvalCount",SchEvalCount);
                    getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));
                    getActivity().finish();

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

                    } else if (webview_url.contains("Message")) {

                        if (flage == true) {
                            flage = false;
                            Url = "javascript:ShowDelMesBtn()";
                            //ButtonText.setText(R.string.string7);
                            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.wancheng));
                            mWebview.loadUrl(Url);

                        } else {
                            Url = "javascript:DelFinish()";
                            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.guanli));
                            flage = true;
                            mWebview.loadUrl(Url);
                        }
                    } else if (webview_url.contains("SelectNews")) {
                        Log.d("fen", webview_url);
                        //这个是获取新闻信息
                        //Url = "javascript:GetNewsInfo()";
                        Url = "javascript:InsertEvaluate()";
                        mWebview.loadUrl(Url);


                    } else if (webview_url.contains("GuessWinLost")) {
                        //EvaluatePage.aspx?SchaduleID=10
                        String s1[] = webview_url.split("\\?");

                        s1 = s1[1].split("\\&");
                        s1 = s1[0].split("\\=");
                        final String ScheduleID = s1[1];
                        String url = ExampleApplication.GetInstance().getString(R.string.MainIP) +
                                "EvaluatePage.aspx?SchaduleID=" +
                                ScheduleID
                                + "&SchEvalCount="
                                + SchEvalCount
                                + "&UserID="
                                + LocalDataObj.GetUserLocalData("UserID")
                                + "&Code="
                                + LocalDataObj.GetUserLocalData("UserToken");

                        Intent intent = new Intent(getActivity(), MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                        intent.putExtra("SchEvalCount", SchEvalCount);
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_EDITPAGEWEBVIEW);
                        startActivityForResult(intent, 0);
                        // startActivity(intent);
                        //getActivity().finish();
                    } else if (webview_url.contains("ChoosePlace")) {
                        //跳转到http://192.168.0.201/webpage/NewPlace.aspx
                        String url = ExampleApplication.GetInstance().getString(R.string.newPlace);
                        Intent intent = new Intent(getActivity(), MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                        startActivityForResult(intent, 0);
                        // getActivity().finish();//当点了新增地址后调到新页并结束掉当前页
                    } else if (webview_url.contains("NewPlace")) {
                        Url = "javascript:InsertRegUsInfo()";
                        mWebview.loadUrl(Url);
                        // getActivity().finish();///////////////////////////////////////////
                    } else if (webview_url.contains("ModiftyAddress")) {
                        Url = "javascript:UpdateRegUsInfo()";
                        mWebview.loadUrl(Url);
                    } else if (webview_url.contains("CashPrizes"))//兑换历史记录
                    {
                        String url = ExampleApplication.GetInstance().getString(R.string.Record);
                        Intent intent = new Intent(getActivity(), MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                        intent.putExtra("SchEvalCount", SchEvalCount + "");
                        startActivityForResult(intent, 0);
                    } else if (webview_url.contains("GuessOrder")) {

                        webview_url = ExampleApplication.GetInstance().getString(R.string.paihangbang_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken");

                        Intent intent = new Intent(getActivity(), MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, webview_url);
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                        startActivityForResult(intent, 0);
                        break;
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

        bt_send = (ImageButton) mContentView.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(button_listener);
        bt_cancle = (ImageButton) mContentView.findViewById(R.id.bt_cancle);
        bt_cancle.setOnClickListener(button_listener);
        comment_text = (EditText) mContentView.findViewById(R.id.comment_text);
        comment_textview = (TextView) mContentView.findViewById(R.id.comment_textview);
        comment_layout = (RelativeLayout) mContentView.findViewById(R.id.comment_layout);

        titleView = (TextView) mContentView.findViewById(R.id.titlename);
        titleView.setText(R.string.maintitle);

        ButtonText = (TextView) mContentView.findViewById(R.id.buttontextView);

        backbutton = (ImageButton) mContentView.findViewById(R.id.back_button);
        backbutton.setOnClickListener(button_listener);
        backbutton.setImageDrawable(getResources().getDrawable(R.drawable.backico_on));


        Rightbutton = (ImageButton) mContentView.findViewById(R.id.right_button);
        Rightbutton.setOnClickListener(button_listener);


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
        mWebSetting.setUseWideViewPort(true);
        mWebSetting.setDefaultTextEncodingName("UTF-8");
        mWebSetting.setLoadWithOverviewMode(true);

        mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSetting.setAppCacheMaxSize(1024 * 1024 * 8);

        mWebSetting.setAllowFileAccess(true);//启用或禁用WebView访问文件数据
        mWebSetting.setAppCacheEnabled(true);

        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebSetting.setSupportZoom(false);
        mWebSetting.setBuiltInZoomControls(false);
        mWebSetting.setGeolocationEnabled(true);
        mWebSetting.setDatabaseEnabled(true);

        mWebSetting.setDomStorageEnabled(true);
        mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.requestFocus();
        mWebview.addJavascriptInterface(new JavaScriptInterface(), "javatojs");

        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {// （Key事件未被加载时调用）
                super.onUnhandledKeyEvent(view, event);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (progressBar == null) {
                    progressBar = ProgressDialog.show(getActivity(), null, "正在加载，请稍后…");
                }
                if (!progressBar.isShowing()) {
                    progressBar.show();
                }
//        progressBar = ProgressDialog.show(MainActivity.GetInstance(), null, "正在加载，请稍后…");
            }

           /* @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

                return super.shouldOverrideKeyEvent(view, event);
            }*/

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
//            MainActivity.GetPushInstance().onRefreshComplete();
                }
            }
        });
        mWebview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
            }
        });
        // titleView.setText(R.string.title1);
        if (msUrl.contains("PageUserInfo")) {
            titleView.setText(R.string.title2);
        }
        if (msUrl.contains("SelectNews")) {
            titleView.setText(R.string.title1); //shareicon
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.shareicon));
            Rightbutton.setVisibility(View.VISIBLE);
            comment_layout.setVisibility(View.VISIBLE);
        }
        if (msUrl.contains("Guess")) {
            titleView.setText(R.string.title3);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            // ButtonText.setText(R.string.string2);
        }

        if (msUrl.contains("GuessWinLost")) {
            titleView.setText(R.string.title3);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.tittlebtn));
            // ButtonText.setText(commentcounts+"评论");//得到品论数目
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);


            new Thread() {
                @Override
                public void run() {

                    String webview_url = msUrl;
                    String s1[] = webview_url.split("\\?");

                    s1 = s1[1].split("\\&");
                    s1 = s1[0].split("\\=");
                    final String ScheduleID = s1[1];
                    int messagecount = 0;

                    String Url = getString(R.string.serverurl) + "/GetCampScheduleInfo";
                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"," +
                            "\"ScheduleID\":" + ScheduleID + "}");
                    String Result = FileUtils.Bytes2String(data);
                    JSONObject Json = JsonUtils.Str2Json(Result);
                    try {
                        Json = Json.getJSONObject("d");
                        Json = Json.getJSONObject("Data");
                        messagecount = Json.getInt("ReplyCount");
                        SchEvalCount = messagecount;//这个是在跳转到下一个页面时需要传入评论的数量
                        Message MSG = new Message();
                        MSG.arg1 = 4;
                        MSG.arg2 = messagecount;
                        updateHandler.sendMessage(MSG);
                    } catch (JSONException e) {
                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                    }


                }
            }.start();

            ButtonText.setText(R.string.string3);

            //GetCampScheduleInfo
        }
        if (msUrl.contains("CampList")) {
            titleView.setText(R.string.title4);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("TopScrollList")) {
            titleView.setText(R.string.title4);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("CampScheduleList")) {
            titleView.setText(R.string.title4);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("Shake")) {
            titleView.setText(R.string.title5);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("Cash")) {
            titleView.setText(R.string.title9);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("CashPrizes")) {
            titleView.setText(R.string.title6);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            // ButtonText.setText(R.string.string1);
            p_PushInstance.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.jilu));
        }


        if (msUrl.contains("Details")) {
            titleView.setText(R.string.title7);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("PlayerInformation")) {
            titleView.setText(R.string.title8);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }

        if (msUrl.contains("DeliveryTime")) {
            titleView.setText(R.string.title10);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("ChoosePlace")) {
            titleView.setText(R.string.title11);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            // ButtonText.setText(R.string.string4);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.newadd));
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("NewPlace")) {
            titleView.setText(R.string.title12);
            Rightbutton.setVisibility(View.VISIBLE);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.queding));
            ButtonText.setVisibility(View.VISIBLE);
            // ButtonText.setText(R.string.string5);

            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("ModiftyAddress")) {
            titleView.setText(R.string.title13);
            Rightbutton.setVisibility(View.VISIBLE);
            ButtonText.setVisibility(View.VISIBLE);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.queding));
            //ButtonText.setText(R.string.string5);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("PageUserInfoData")) {
            titleView.setText(R.string.title14);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("Evaluate")) {
            titleView.setText(R.string.title15);
        }
        if (msUrl.contains("ReplyEvaluate")) {
            titleView.setText(R.string.title15);
        }
        if (msUrl.contains("UpdatePwassword")) {
            titleView.setText(R.string.title16);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("Login")) {
            titleView.setText(R.string.title17);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("Register")) {
            titleView.setText(R.string.title18);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("Record")) {
            titleView.setText(R.string.title19);
        }
        if (msUrl.contains("SendEmail")) {
            titleView.setText(R.string.title27);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("AccountManager")) {
            titleView.setText(R.string.title20);
            Rightbutton.setVisibility(View.VISIBLE);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.zhanghaoedit));
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("MyCard")) {
            titleView.setText(R.string.title21);
        }
        if (msUrl.contains("Order")) {
            titleView.setText(R.string.title22);
        }
        if (msUrl.contains("GuessOrder")) {
            //titleView.setText(R.string.title3);
            titleView.setText(R.string.title28);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.caifubang));
        }
        if (msUrl.contains("Message")) {
            titleView.setText(R.string.title23);
            Rightbutton.setVisibility(View.VISIBLE);
            Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.guanli));
            ButtonText.setVisibility(View.VISIBLE);
            //ButtonText.setText(R.string.string6);
        }
        if (msUrl.contains("AboutUs")) {
            titleView.setText(R.string.title24);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("singleguest")) {//个人中心的腾讯微博链接
            titleView.setText("");
        }
        if (msUrl.contains("AboutPage")) {//个人中心的微信链接
            titleView.setText("");
        }
        if (msUrl.contains("cslapp")) {
            titleView.setText("");
        }
        if (msUrl.contains("SelectSoftware")) {//软件推荐
            titleView.setText(R.string.title25);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msUrl.contains("GoldRankPage")) {
            titleView.setText(R.string.title26);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (msTitle != null) {
            titleView.setText(msTitle);
            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
            msTitle = null;
        }
        mWebview.loadUrl(msUrl);
        obtainData();
    }

    public void updatecomment(int counts) {
        ButtonText.setText(counts + "评论");
        String uri = msUrl;
        mWebview.loadUrl(uri);
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
                {
                    Toast.makeText(getContentView().getContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                   // startActivityForResult(intent, Activity.RESULT_CANCELED);
                    startActivity(intent);
                }
                    getActivity().finish();
                break;
                case 2:    //通知页面停止刷新
                    if (p_PushInstance.isRefreshing())
                        p_PushInstance.onRefreshComplete();
                    break;
                case 3://页面重新刷新
                    comment_text.setText("");//清空
                    comment_textview.setText("发布评论");
                    if (comment_layout.getVisibility() == View.VISIBLE) {
                        comment_layout.setVisibility(View.GONE);
                    } else {
                        comment_layout.setVisibility(View.VISIBLE);
                    }
                    alertTips("发送成功");
                    mWebview.reload();
                    break;
                case 4://刷新评论数量

                    ButtonText.setText(msg.arg2 + "评论");
//                    ButtonText.setText(R.string.string3)
                    break;
                case 5://显示错误提示
                {
                    alertTips(GetStringTips.GetString(msg.arg2));
                }
                break;
                case 6://注册以后的返回
                {
                    Toast.makeText(getContentView().getContext(),"注册并登陆成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
//                    startActivityForResult(intent, Activity.RESULT_CANCELED);
                    startActivity(intent);
                   getActivity().finish();
                }
                break;
                case 7://新增地址成功后跳转ChoosePlace页面

                    msUrl = ExampleApplication.GetInstance().getString(R.string.MainIP)
                            + "ChoosePlace"
                            + ".aspx"
                            + "?"
                            + "UserID="
                            + LocalDataObj.GetUserLocalData("UserID")
                            + "&Code="
                            + LocalDataObj.GetUserLocalData("UserToken");
                    mWebview.loadUrl(msUrl);
                    if (msg.arg2 == EDIT_SUCCESS) {
                        Toast.makeText(getContentView().getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg2 == ADD_SUCCESS) {
                        Toast.makeText(getContentView().getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 8: {
                    comment_layout.setVisibility(View.VISIBLE);
                    comment_textview.setText(comment);
                    break;
                }
                case 9: {//新浪授权登陆
                    UMengUtils.SSOLogin(SHARE_MEDIA.SINA, updateHandler, getActivity());
                    break;
                }
                case 10: {//腾讯授权登陆
                    UMengUtils.SSOLogin(SHARE_MEDIA.TENCENT, updateHandler, getActivity());
                    break;
                }
                case 11://添加分享平台和内容
                    UMengUtils.InitUMengConfig(mContentView.getContext());//添加分享平台
                    UMengUtils.ShareContent(shareCount);//添加分享内容
                    break;
                case 12:
                    getActivity().finish();
                    break;
                case 13: {
                    Rightbutton.setVisibility(View.GONE);
                    if (msUrl.contains("Cash")) {

                        titleView.setText(R.string.title9);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                    }
                    if (msUrl.contains("ChoosePlace")) {
                        titleView.setText(R.string.title11);
                        Rightbutton.setVisibility(View.VISIBLE);
                        ButtonText.setVisibility(View.VISIBLE);
                        // ButtonText.setText(R.string.string4);
                        Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.newadd));
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                    }
                    if (msUrl.contains("NewPlace")) {
                        titleView.setText(R.string.title12);
                        Rightbutton.setVisibility(View.VISIBLE);
                        Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.queding));
                        ButtonText.setVisibility(View.VISIBLE);
                        // ButtonText.setText(R.string.string5);

                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                    }
                }
                break;
                case 14:
                   // errorCode = msg.arg2;
                    alertTips("1");
                    break;
            }
        }
    };


    private void obtainData() {
        // Show indeterminate progress
        setContentShown(false);
    }

    public void alertTips(String mes) {

        comment_text.setText("");//清空
        comment_textview.setText("发布评论");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContentView().getContext());
        builder.setTitle("中超社区");
        if (mes.equals("内容不能为空")) {
            builder.setMessage(mes);
            builder.setPositiveButton("确定", null);
            builder.show();
        } else if (mes.equals("匿名账号不能发送")) {
            builder.setMessage(mes);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getContentView().getContext(), MainFragment.class);
                    intent.putExtra(MainFragment.EXTRA_VIEW_URL, ExampleApplication.GetInstance().getString(R.string.denglu_view));
                    intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

                    startActivityForResult(intent, 0);
                }
            });
            builder.show();
        } else if (mes.equals("发送成功")) {
            builder.setMessage(mes);
            builder.setPositiveButton("确定", null);
            builder.show();
        } else if (mes.equals("此账号不存在")) {
            builder.setMessage(mes);
            builder.setPositiveButton("确定", null);
            builder.show();
        } else if (mes.equals("1")) {
            builder.setMessage(GetStringTips.GetString(errorCode));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  //  getActivity().finish();
                }
            });
            builder.show();
        } else {
            builder.setMessage(mes);
            builder.setPositiveButton("确定", null);
            builder.show();
        }
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
                            shareCount = objectParams.getString("Content");
                            Message MSG = new Message();
                            MSG.arg1 = 11;
                            updateHandler.sendMessage(MSG);
                            break;

                        }

                        if (Name.equalsIgnoreCase("SendMailToUser") == true) {//点击了私信之后输入框
                            objectParams = JsonUtils.Str2Json(Params);
                            RecUserId = objectParams.getString("UserID");
                            Message MSG = new Message();
                            MSG.arg1 = 8;
                            updateHandler.sendMessage(MSG);
                            break;
                        }
                        if (Name.equalsIgnoreCase("UpdateRegUsInfo")) {//修改地址
                            LocalDataObj.SetUserLocalData("Send_Params", Params);
                            new Thread() {
                                @Override
                                public void run() {
                                    String Url = getString(R.string.serverurl) + "/UpdateRegUsInfo";
                                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, LocalDataObj.GetUserLocalData("Send_Params"));
                                    String Result = FileUtils.Bytes2String(data);
                                    JSONObject Json = JsonUtils.Str2Json(Result);
                                    try {
                                        Json = Json.getJSONObject("d");
                                        String Code = Json.getString("Code");
                                        if (Code.equals("0")) {
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("ChangeState", 2);
                                            getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));

                                            Message MSG = new Message();
                                            MSG.arg1 = 7;
                                            MSG.arg2 = EDIT_SUCCESS;
                                            updateHandler.sendMessage(MSG);


                                        } else {
                                            int errcode = Integer.parseInt(Code);

                                            Message MSG = new Message();
                                            MSG.arg1 = 5;
                                            MSG.arg2 = errcode;
                                            updateHandler.sendMessage(MSG);

                                        }
                                    } catch (JSONException e) {
                                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                                    }
                                }
                            }.start();
                            break;
                        }
                        if (Name.equalsIgnoreCase("InsertRegUsInfo")) {//新增地址
                            LocalDataObj.SetUserLocalData("Send_Params", Params);
                            new Thread() {
                                @Override
                                public void run() {
                                    String Url = getString(R.string.serverurl) + "/InsertRegUsInfo";
                                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, LocalDataObj.GetUserLocalData("Send_Params"));
                                    String Result = FileUtils.Bytes2String(data);
                                    JSONObject Json = JsonUtils.Str2Json(Result);
                                    try {
                                        Json = Json.getJSONObject("d");
                                        String Code = Json.getString("Code");
                                        if (Code.equals("0")) {
                                           /* Bundle bundle = new Bundle();
                                            bundle.putInt("ChangeState", 2);
                                            getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));*/
                                            Message MSG = new Message();
                                            MSG.arg1 = 7;
                                            MSG.arg2 = ADD_SUCCESS;
                                            updateHandler.sendMessage(MSG);
                                        } else {
                                            int errcode = Integer.parseInt(Code);

                                            Message MSG = new Message();
                                            MSG.arg1 = 5;
                                            MSG.arg2 = errcode;
                                            updateHandler.sendMessage(MSG);

                                        }
                                    } catch (JSONException e) {
                                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                                    }
                                }
                            }.start();
                            break;
                        }
                        if (Name.equalsIgnoreCase("GetNewsInfo") == true) {
                            objectParams = JsonUtils.Str2Json(Params);
                            // Log(objectParams)
                            Log.d("CH", objectParams + "");
                            break;
                        }
                        if (Name.equalsIgnoreCase("ReplayMessage")) {//我的消息中的回复
                            objectParams = JsonUtils.Str2Json(Params);
                            RecUserId = objectParams.getString("UserID");
                            UserNick = objectParams.getString("UserNick");
                            comment = "回复" + objectParams.getString("UserNick") + ":";
                            Message MSG = new Message();
                            MSG.arg1 = 8;
                            updateHandler.sendMessage(MSG);
                            break;
                        }
                        if (Name.equalsIgnoreCase("ReplayEval") == true)//这个是点击回复接口
                        {
                            objectParams = JsonUtils.Str2Json(Params);
                            EvalType = objectParams.getString("EvalType");
                            sNewsID = objectParams.getString("ID");
                            EvalID = objectParams.getInt("EvalID");
                            comment = "回复" + objectParams.getString("UserNick") + ":";
                            Message MSG = new Message();
                            MSG.arg1 = 8;
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
                        if (Name.equalsIgnoreCase("ChooseUser") == true) //选择用户登陆
                        {
                            objectParams = JsonUtils.Str2Json(Params);
                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                            JSONArray userArray = JsonUtils.Str2JsonArray(jsonStr);
                            String nameList = "";
                            JSONObject user = null;
                            for (int i = 0; i < userArray.length(); i++) {
                                user = (JSONObject) userArray.get(i);
                                String username = objectParams.getString("UserNick");
                                if (username.equalsIgnoreCase(user.getString("UserNick"))) {
                                    break;
                                }
                                user = null;
                            }
                            if (user != null) {
                                String uid = user.getString("UserID");
                                String Code = user.getString("Code");
                                String UserNick = user.getString("UserNick");
                                String UserHeadImg = user.getString("UserHeadImg");
                                String UserSex = user.getString("UserSex");
                                LocalDataObj.SetUserLocalData("UserID", uid);
                                LocalDataObj.SetUserLocalData("UserToken", Code);
                                LocalDataObj.SetUserLocalData("UserNick", UserNick);
                                LocalDataObj.SetUserLocalData("UserHeadImg", UserHeadImg);
                                LocalDataObj.SetUserLocalData("UserSex", UserSex);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putInt("ChangeState", 1);
                            getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));
                            Message MSG = new Message();
                            MSG.arg1 = 1;
                            updateHandler.sendMessage(MSG);
                        }
                        if (Name.equalsIgnoreCase("GetUserAccount") == true) {
                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                            JSONArray userArray = JsonUtils.Str2JsonArray(jsonStr);
                            String nameList = "";
                            for (int i = 0; i < userArray.length(); i++) {
                                JSONObject user = (JSONObject) userArray.get(i);

                                nameList += user.getString("UserNick");
                                if (i != userArray.length() - 1)
                                    nameList += ",";
                            }
                            String Url = "javascript:GetUserAccount(" + jsonStr + "," + LocalDataObj.GetUserLocalData("UserID") + ")";
                            mWebview.loadUrl(Url);
                            break;
                        }
                        if (Name.equalsIgnoreCase("UserRegister") == true) {

                            LocalDataObj.SetUserLocalData("Send_Params", Params);
                            new Thread() {
                                @Override
                                public void run() {

                                    String Url = getString(R.string.serverurl) + "/UserRegister";
                                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, LocalDataObj.GetUserLocalData("Send_Params"));
                                    String Result = FileUtils.Bytes2String(data);
                                    JSONObject Json = JsonUtils.Str2Json(Result);
                                    try {
                                        Json = Json.getJSONObject("d");
                                        String Code = Json.getString("Code");

                                        if (Code.equals("0")) {
                                            Json = Json.getJSONObject("Data");
                                            Code = Json.getString("Code");
                                            String UserNick = Json.getString("UserNick");
                                            String uid = Json.getString("UserId");
                                            String UserName = Json.getString("UserName");
                                            String UserHeadImg = Json.getString("UserHeadImg");
                                            String UserSex = Json.getString("UserSex");
                                            LocalDataObj.SetUserLocalData("UserToken", Code);
                                            LocalDataObj.SetUserLocalData("UserID", uid);
                                            LocalDataObj.SetUserLocalData("UserNick", UserNick);
                                            LocalDataObj.SetUserLocalData("UserHeadImg", UserHeadImg);
                                            LocalDataObj.SetUserLocalData("UserSex", UserSex);
                                            uid = LocalDataObj.GetUserLocalData("UserID");
                                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                                            JSONArray userArray;
                                            if (jsonStr.equalsIgnoreCase("") == true) {
                                                userArray = new JSONArray();
                                            } else {
                                                userArray = JsonUtils.Str2JsonArray(jsonStr);
                                            }
                                            JSONObject user = new JSONObject();
                                            user.put("UserID", uid);
                                            user.put("Code", Code);
                                            user.put("UserNick", UserNick);
                                            user.put("UserName", UserName);
                                            user.put("UserHeadImg", UserHeadImg);
                                            user.put("UserSex", UserSex);
                                            userArray.put(user);
                                            String userJsonStr = userArray.toString();
                                            LocalDataObj.SetUserLocalData("LocalUserJson", userJsonStr);

                                            Bundle bundle = new Bundle();
                                            bundle.putInt("ChangeState", 2);
                                            getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));
                                            Message MSG = new Message();
                                            MSG.arg1 = 6;
                                            updateHandler.sendMessage(MSG);


                                        } else {
                                            int errcode = Integer.parseInt(Code);

                                            Message MSG = new Message();
                                            MSG.arg1 = 5;
                                            MSG.arg2 = errcode;
                                            updateHandler.sendMessage(MSG);

                                        }
                                    } catch (JSONException e) {
                                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                                    }
                                }
                            }.start();
                            break;
                        }
                        if (Name.equalsIgnoreCase("sinalogin") == true) {//第三方登陆
                            //UMengUtils.InitUMengConfig(mContentView.getContext());
                            Message MSG = new Message();
                            MSG.arg1 = 9;
                            updateHandler.sendMessage(MSG);

                        }
                        if (Name.equalsIgnoreCase("qqlogin")) {
                            Message MSG = new Message();
                            MSG.arg1 = 10;
                            updateHandler.sendMessage(MSG);
                        }
                        if (Name.equalsIgnoreCase("UserLogin") == true) {

                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                            if (jsonStr.equalsIgnoreCase("") == false) {
                                objectParams = JsonUtils.Str2Json(Params);
                                JSONArray userArray;
                                userArray = JsonUtils.Str2JsonArray(jsonStr);
                                for (int i = 0; i < userArray.length(); i++) {
                                    JSONObject user = (JSONObject) userArray.get(i);
                                    String UserName = user.getString("UserName");
                                    String loginUserName = objectParams.getString("UserName");
                                    if (UserName.equalsIgnoreCase(loginUserName) == true) {
                                        String uid = user.getString("UserID");
                                        String Code = user.getString("Code");
                                        String UserNick = user.getString("UserNick");
                                        String UserHeadImg = Json.getString("UserHeadImg");
                                        String UserSex = Json.getString("UserSex");
                                        LocalDataObj.SetUserLocalData("UserID", uid);
                                        LocalDataObj.SetUserLocalData("UserToken", Code);
                                        LocalDataObj.SetUserLocalData("UserNick", UserNick);
                                        LocalDataObj.SetUserLocalData("UserHeadImg", UserHeadImg);
                                        LocalDataObj.SetUserLocalData("UserSex", UserSex);
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("ChangeState", 2);
                                        getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));

                                        Message MSG = new Message();
                                        MSG.arg1 = 1;
                                        updateHandler.sendMessage(MSG);
                                        return;
                                    } /*else {
                                        alertTips("1");
                                        return;
                                    }*/
                                }
//                                break;
                            }

                            LocalDataObj.SetUserLocalData("Send_Params", Params);
                            new Thread() {
                                @Override
                                public void run() {
//你要执行的方法
//执行完毕后给handler发送一个空消息
                                    String Url = getString(R.string.serverurl) + "/UserLogin";
                                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, LocalDataObj.GetUserLocalData("Send_Params"));
                                    String Result = FileUtils.Bytes2String(data);
                                    JSONObject Json = JsonUtils.Str2Json(Result);
                                    try {
                                        Json = Json.getJSONObject("d");
                                        errorCode = Json.getInt("Code");
                                        if (errorCode != 0) {
                                            Message MSG = new Message();
                                            MSG.arg1 = 14;
                                            updateHandler.sendMessage(MSG);
                                             return;
                                        }
                                            Json = Json.getJSONObject("Data");
                                            String uid = Json.getString("UserId");
                                            String Code = Json.getString("Code");
                                            String UserNick = Json.getString("UserNick");
                                            String UserName = Json.getString("UserName");
                                            String UserHeadImg = Json.getString("UserHeadImg");
                                            String UserSex = Json.getString("UserSex");
                                            LocalDataObj.SetUserLocalData("UserID", uid);
                                            LocalDataObj.SetUserLocalData("UserToken", Code);
                                            LocalDataObj.SetUserLocalData("UserNick", UserNick);
                                            LocalDataObj.SetUserLocalData("UserHeadImg", UserHeadImg);
                                            LocalDataObj.SetUserLocalData("UserSex", UserSex);

                                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                                            JSONArray userArray;
                                            if (jsonStr.equalsIgnoreCase("") == true) {
                                                userArray = new JSONArray();
                                            } else {
                                                userArray = JsonUtils.Str2JsonArray(jsonStr);
                                            }
                                            JSONObject user = new JSONObject();
                                            user.put("UserID", uid);
                                            user.put("Code", Code);
                                            user.put("UserNick", UserNick);
                                            user.put("UserName", UserName);
                                            user.put("UserHeadImg", UserHeadImg);
                                            user.put("UserSex", UserSex);
                                            userArray.put(user);
                                            String userJsonStr = userArray.toString();
                                            LocalDataObj.SetUserLocalData("LocalUserJson", userJsonStr);


                                    } catch (JSONException e) {
                                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                                    }

                                Bundle bundle = new Bundle();
                                bundle.putInt("ChangeState", 2);
                                getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));

                                Message MSG = new Message();
                                MSG.arg1 = 1;
                                updateHandler.sendMessage(MSG);

                                }
                            }.start();
                            break;
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
                                if (url.contains("Cash")) {
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt("ChangeState", 2);
//                                    bundle.putString("chageurl", url);
//                                    getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent().putExtras(bundle));
//                                    getActivity().finish();
                                    msUrl = url;
                                    mWebview.loadUrl(url);
                                    Message MSG = new Message();
                                    MSG.arg1 = 13;
                                    updateHandler.sendMessage(MSG);
                                } else if(url.contains("PageUserInfo")){
                                    Intent intent = new Intent(getActivity(), MainFragment.class);
                                    intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                    intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                                    startActivity(intent);
                                }
                                else {
                                    mWebview.loadUrl(url);
                                }


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

                                    if (url.contains("SelectNews")) {
                                        Intent intent = new Intent(getActivity(), MainFragment.class);
                                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_EDITPAGEWEBVIEW);
                                        startActivity(intent);
                                    } else if (url.contains("EvaluatePage")) {//从评论管理跳到竞猜品论页
                                        Intent intent = new Intent(getActivity(), MainFragment.class);
                                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_EDITPAGEWEBVIEW);
                                        startActivity(intent);
                                    } else if (url.contains("PageUserInfoData")) {//修改密码点了取消之后
                                        Message MSG = new Message();
                                        MSG.arg1 = 12;
                                        updateHandler.sendMessage(MSG);
                                    } else if (msUrl.contains("Details") && url.contains("Cash")) {
                                        Intent intent = new Intent(getActivity(), MainFragment.class);
                                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                                        startActivity(intent);
                                    } else if (msUrl.contains("DeliveryTime") || url.contains("DeliveryTime") || url.contains("ChoosePlace") || url.contains("NewPlace")) {

                                        msUrl = url;
                                        mWebview.loadUrl(url);
                                        Message MSG = new Message();
                                        MSG.arg1 = 13;
                                        updateHandler.sendMessage(MSG);
                                    } else {
                                        Intent intent = new Intent(getActivity(), MainFragment.class);
                                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                                        startActivity(intent);
                                    }
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
