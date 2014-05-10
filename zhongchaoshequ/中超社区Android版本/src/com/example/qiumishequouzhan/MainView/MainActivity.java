package com.example.qiumishequouzhan.MainView;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.example.qiumishequouzhan.R;
import com.example.qiumishequouzhan.*;
import com.example.qiumishequouzhan.Utils.*;
import com.example.qiumishequouzhan.webviewpage.MainFragment;
import com.example.qiumishequouzhan.webviewpage.OneWebPageView;
import com.gotye.bean.GotyeSex;
import com.gotye.sdk.GotyeSDK;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.umeng.fb.FeedbackAgent;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends BaseListMenu {
    /**
     * Called when the activity is first created.
     */

    ShakeListener mShakeListener = null;
    public static long lastUpdateTime;
    private String pamars;
    public static WebView obj_web;
    private static MainActivity p_MainActivity;
    private static PullToRefreshWebView p_PushInstance;
    public static Bitmap HeadPic = null;
    public TextView titleView;
    private boolean isExit = false;
    public TextView ButtonText;
    public ImageButton Rightbutton;
    public ImageButton bt_openmenu;
    private String titlecount;
    public boolean isInMainView;
    private String DeviceId;
    private SoundPool sp;//声明一个SoundPool
    private int music, music2;//；来设置suondID
    public String shareCount;//分享的内容
    public String imgPath;
    public String pathURL;
    public static int shakecount;
    public String startName;
    public static String usercountUrl;
    public int select_menu_index;
    private Handler NetWorkhandler = new Handler() {
        @Override
//当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//处理UI
            obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.MainView) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
        }
    };

    public static PullToRefreshWebView GetPushInstance() {
        return p_PushInstance;
    }

    private void setListeners() {
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {

                long currentUpdateTime = System.currentTimeMillis();
                long timeInterval = currentUpdateTime - lastUpdateTime;
                if (timeInterval < 3000)
                    return;
                lastUpdateTime = currentUpdateTime;

                //开始调用摇一摇接口  Shake.aspx
                String url = obj_web.getUrl();
                if (url.contains("Shake") && isInMainView == true) {
//                    url = obj_web.getUrl();
                    String uid = LocalDataObj.GetUserLocalData("UserID");
                    if (uid.equalsIgnoreCase("100") == true) {   //说明是游客用户
                        Intent intent = new Intent(MainActivity.this, MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, ExampleApplication.GetInstance().getString(R.string.denglu_view));
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

//                         startActivity(intent);
                        startActivityForResult(intent, 0);
                    } else {
                        if (shakecount <= 0) {
                            Toast.makeText(MainActivity.this, "请购买次数", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            sp.play(music, 1, 1, 0, 0, 1);//播放声音
                            new Thread() {
                                @Override
                                public void run() {
                                    //你要执行的方法
                                    //执行完毕后给handler发送一个空消息
                                    String Url = getString(R.string.serverurl) + "/ShakeStarIfno";
                                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                                            " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"}");
                                    String Result = FileUtils.Bytes2String(data);
                                    int nTemp = 0;
                                    JSONObject Json = JsonUtils.Str2Json(Result);
                                    try {
                                        Json = Json.getJSONObject("d");
                                        Json = Json.getJSONObject("Data");
                                        // shakecount = Json.getInt("ShakeCount");
                                        startName = Json.getString("StarName");
                                        try {
                                            sleep(800);
                                            sp.pause(music);
                                            if (!"".equals(startName)) {
                                                sp.play(music2, 1, 1, 0, 0, 1);
                                            }
                                            Url = "javascript:ShakeStarIfno(" + Result + ")";
                                            obj_web.loadUrl(Url);
                                            shakecount--;
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    } catch (JSONException e) {
                                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                                    }
                                }
                            }.start();
                        }
                    }
                }
            }
        });
    }

    private void initState() {
        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量

        music = sp.load(this, R.raw.shake_sound_male, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        music2 = sp.load(this, R.raw.shake_sound, 1);
        mShakeListener = new ShakeListener(this);
        getShakecount();
        setListeners();

    }

    View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.right_button:
                    String url = obj_web.getUrl();
                    if (url.contains("Guess")) {
                        url = ExampleApplication.GetInstance().getString(R.string.paihangbang_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken");

                        Intent intent = new Intent(MainActivity.this, MainFragment.class);
                        intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

//                        startActivity(intent);
                        startActivityForResult(intent, 0);
                    }

                    break;
                case R.id.bt_openmenu:

                    mMenuDrawer.openMenu();

                    getNewsCounts();//这个是得到个人中心的counts
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInMainView = true;
        p_MainActivity = this;
        mMenuDrawer.setContentView(R.layout.main);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        mMenuDrawer.setDrawerIndicatorEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bt_openmenu = (ImageButton) findViewById(R.id.bt_openmenu);
        bt_openmenu.setOnClickListener(button_listener);

        p_PushInstance = (PullToRefreshWebView) findViewById(R.id.webView);
        titleView = (TextView) findViewById(R.id.titlename);
        titleView.setText(R.string.maintitle);

        ButtonText = (TextView) findViewById(R.id.buttontextView);

        Rightbutton = (ImageButton) findViewById(R.id.right_button);
        Rightbutton.setOnClickListener(button_listener);
        //  Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.tittlebtn));
        Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.caifubang));

        Rightbutton.setVisibility(View.GONE);
        ButtonText.setVisibility(View.GONE);

        p_PushInstance.setGravity(Gravity.BOTTOM);
        obj_web = p_PushInstance.getRefreshableView();
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
                obj_web.reload();
                p_PushInstance.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<WebView> refreshView) {
                SetRefreshTitle(true);

                String Url = "javascript:OnLoad()";
                obj_web.loadUrl(Url);

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
        obj_web.requestFocus();
        WebSettings mWebSetting = obj_web.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
//        mWebSetting.setUseWideViewPort(true);
        mWebSetting.setDefaultTextEncodingName("UTF-8");
//        mWebSetting.setLoadWithOverviewMode(true);

//        mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
//        mWebSetting.setAppCacheMaxSize(1024 * 1024 * 8);
//        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
//        mWebSetting.setAppCachePath(appCacheDir);
//        mWebSetting.setAllowFileAccess(true);
//        mWebSetting.setAppCacheEnabled(true);
//        //设置加载进来的页面自适应手机屏幕
//        mWebSetting.setUseWideViewPort(true);
//        mWebSetting.setLoadWithOverviewMode(true);
//
//        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
//        mWebSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);

//        mWebSetting.setSupportZoom(false);
//        mWebSetting.setBuiltInZoomControls(false);
//        mWebSetting.setGeolocationEnabled(true);
//        mWebSetting.setDatabaseEnabled(true);
//        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        mWebSetting.setDatabasePath(dir);
//        mWebSetting.setDomStorageEnabled(true);
//        obj_web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        obj_web.addJavascriptInterface(new JavaScriptInterface(), "javatojs");
        obj_web.setWebChromeClient(new UserWebClient());
        obj_web.setWebViewClient(new NormalWebViewClient(this));

        obj_web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                MainActivity.GetInstance().startActivity(intent);
            }
        });

        mMenuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(View v, int dx, int x, int y) {
                getNewsCounts();//这个是得到个人中心的counts
                return v instanceof SeekBar;
            }
        });
        String uid = LocalDataObj.GetUserLocalData("UserID");
        if (uid.equalsIgnoreCase("") == true) {
            new Thread() {
                @Override
                public void run() {
                    //你要执行的方法
                    //执行完毕后给handler发送一个空消息
                    String Url = getString(R.string.serverurl) + "/AutoLogin";
                    byte[] data = HttpUtils.GetWebServiceJsonContent(Url, null);
                    String Result = FileUtils.Bytes2String(data);
                    int nTemp = 0;
                    JSONObject Json = JsonUtils.Str2Json(Result);
                    try {
                        Json = Json.getJSONObject("d").getJSONObject("Data");
                        String uid = Json.getString("UserId");
                        String Code = Json.getString("Code");
                        LocalDataObj.SetUserLocalData("UserID", uid);
                        LocalDataObj.SetUserLocalData("UserToken", Code);


                    } catch (JSONException e) {
                        LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                    }
                    NetWorkhandler.sendEmptyMessage(0);
                }
            }.start();
        } else {
            NetWorkhandler.sendEmptyMessage(0);
        }
        initState();
        getNewsCounts();//这个是得到个人中心的counts
    }

    public void getShakecount() {
        new Thread() {
            @Override
            public void run() {
                //获取摇一摇之前的次数
                String Url = getString(R.string.serverurl) + "/GetShakeCount";
                byte[] data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                        " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"}");
                String Result = FileUtils.Bytes2String(data);
                JSONObject Json = JsonUtils.Str2Json(Result);
                try {
                    if (Json == null) {
                        return;
                    }
                    Json = Json.getJSONObject("d");
                    Json = Json.getJSONObject("Data");
                    int num = Json.getInt("ShakeCount");
                    Message MSG = new Message();
                    MSG.arg1 = 5;
                    MSG.arg2 = num;
                    updateHandler.sendMessage(MSG);

                } catch (JSONException e) {
                    LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                }
            }
        }.start();
    }

    public void getNewsCounts() {
        new Thread() {
            @Override
            public void run() {
                //执行完毕后给得到个人中心的评论条数
                String Url = getString(R.string.serverurl) + "/GetUserCanReadData";
                byte[] data = HttpUtils.GetWebServiceJsonContent(Url, "{\"UserId\":" + LocalDataObj.GetUserLocalData("UserID") + "," +
                        " \"Code\":\"" + LocalDataObj.GetUserLocalData("UserToken") + "\"}");
                String Result = FileUtils.Bytes2String(data);
                JSONObject Json = JsonUtils.Str2Json(Result);
                try {
                    if (Json == null) {
                        return;
                    }
                    Json = Json.getJSONObject("d");//.getJSONObject("Data");
                    JSONArray jsonArray = Json.getJSONArray("Data");
                    int news = jsonArray.getInt(0);
                    int commentManagerCounts = jsonArray.getInt(1);
                    int counts = news + commentManagerCounts;

                    Message MSG = new Message();
                    MSG.arg1 = 3;
                    MSG.arg2 = counts;
                    updateHandler.sendMessage(MSG);
                } catch (JSONException e) {
                    LogUitls.WriteLog("FileUtils", "WriteFile2Store", Json.toString(), e);
                }
            }
        }.start();

    }

    //创建Handler对象，用来处理消息
    Handler mExitHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {//处理消息
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public void SendCallBack(String path) {
        String Url = "javascript:SetHeadPhoto(\"" + path + "\")";
        obj_web.loadUrl(Url);
    }

    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (keyCoder == KeyEvent.KEYCODE_BACK) {

            ToQuitTheApp();
            return false;
        }
        return true;
    }

    //封装ToQuitTheApp方法
    private void ToQuitTheApp() {
        if (isExit) {
            // ACTION_MAIN with category CATEGORY_HOME 启动主屏幕
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);// 使虚拟机停止运行并退出程序
        } else {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出APP", Toast.LENGTH_SHORT).show();
            mExitHandler.sendEmptyMessageDelayed(0, 3000);// 3秒后发送消息
        }
    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1://登陆wancheng
//                    Intent intent=new Intent();
//                    intent.setClass(MainFragment.GetInstance(), MainActivity.class);
//                    startActivity(intent);
                    MainFragment.GetInstance().finish();
                    break;
                case 2:    //通知页面停止刷新
                    if (p_PushInstance.isRefreshing())
                        p_PushInstance.onRefreshComplete();
                    break;
                case 3://通知menu的新消息数量
                    titlecount = msg.arg2 + "";//得到个人中新消息数目

                    MenuAdapter.titleCount[6] = titlecount;
                    mAdapter.notifyDataSetChanged();
                    break;
                case 4://个人中心每日任务的邀请好友分享
                    UMengUtils.InitUMengConfig(p_MainActivity);
                    UMengUtils.ShareContent(shareCount, ExampleApplication.GetInstance().getString(R.string.BaseIP) +
                            imgPath, ExampleApplication.GetInstance().getString(R.string.BaseIP) +
                            pathURL);//添加分享能容
                    break;
                case 5://获取摇一摇的次数
                    shakecount = msg.arg2;
                    break;
            }
        }
    };

    public static MainActivity GetInstance() {
        return p_MainActivity;
    }

    public static WebView GetWebView() {
        return obj_web;
    }

    @Override
    protected void onMenuItemClicked(int position, Item item) {
//        mContentTextView.setText(item.mTitle);

        Rightbutton.setVisibility(View.GONE);
        ButtonText.setVisibility(View.GONE);
        View view =LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_row_category, null, false);
        View view1 =LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_row_item, null, false);
        view.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);
        mMenuDrawer.closeMenu();

        select_menu_index = position;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (select_menu_index) {
                    case 0:    //中超资讯
                        obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.MainView) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
                        titleView.setText(R.string.maintitle);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.BOTH);
                        break;
                    case 1:    //中超竞猜
                        obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.jingcai_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
                        titleView.setText(R.string.jingcaititle);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);

                        Rightbutton.setVisibility(View.VISIBLE);
                        ButtonText.setVisibility(View.VISIBLE);
                        // ButtonText.setText(R.string.string2);
                        ButtonText.setText("");

                        break;
                    case 2:    //中超球队
                        obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.qiudui_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
                        titleView.setText(R.string.quiduititle);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                        break;
                    case 3:    //语音聊球
                    {
                        String uid = LocalDataObj.GetUserLocalData("UserID");
                        if (uid.equalsIgnoreCase("100") == true) {   //说明是游客用户
                            Intent intent = new Intent(MainActivity.this, MainFragment.class);
                            intent.putExtra(MainFragment.EXTRA_VIEW_URL, ExampleApplication.GetInstance().getString(R.string.denglu_view));
                            intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

                            startActivityForResult(intent, 0);
                        } else {
                            //LocalDataObj.GetUserLocalData("UserHeadImg")  LocalDataObj.GetUserLocalData("UserID")  LocalDataObj.GetUserLocalData("UserNick")  LocalDataObj.GetUserLocalData("UserSex")
                            Bitmap head = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.ic_launcher);
//                    returnBitMap(ExampleApplication.GetInstance().getString(R.string.MainIP)+LocalDataObj.GetUserLocalData("UserHeadImg"))
                            GotyeSDK.getInstance().startGotyeSDK(MainActivity.this, LocalDataObj.GetUserLocalData("UserID"), LocalDataObj.GetUserLocalData("UserNick"), GotyeSex.NOT_SET, head, null);
                        }
                    }
                    break;
                    case 4:    //趣味竞猜
                        obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.quweijingcai_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
                        titleView.setText(R.string.quweijingcaititle);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                        break;
                    case 5:    //摇球星卡
                        obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.qiuxingka_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
                        titleView.setText(R.string.qiuxingkatitle);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                        break;

                    case 6:    //个人中心
                        String uid = LocalDataObj.GetUserLocalData("UserID");
                        if (uid.equalsIgnoreCase("100") == true) {   //说明是游客用户
                            Intent intent = new Intent(MainActivity.this, MainFragment.class);
                            intent.putExtra(MainFragment.EXTRA_VIEW_URL, ExampleApplication.GetInstance().getString(R.string.denglu_view));
                            intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

//                         startActivity(intent);
                            startActivityForResult(intent, 0);
                        } else {
                            String sUrl = ExampleApplication.GetInstance().getString(R.string.gerenzhongxin_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken");
                            obj_web.loadUrl(sUrl);
                            titleView.setText(R.string.gerenzhongxintitle);
                            p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                        }
                        break;
                    case 7:    //关于我们
                        obj_web.loadUrl(ExampleApplication.GetInstance().getString(R.string.guanyuwomen_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken"));
                        titleView.setText(R.string.guanyuwomentitle);
                        p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                        break;
                }
            }
        }, 500);

        // mMenuDrawer.closeMenu();

    }

    public final static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;

        try {
            myFileUrl = new URL(url);
            HttpURLConnection conn;

            conn = (HttpURLConnection) myFileUrl.openConnection();

            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected int getDragMode() {
        return MenuDrawer.MENU_DRAG_CONTENT;
    }

    @Override
    protected Position getDrawerPosition() {
        return Position.LEFT;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString(STATE_CONTENT_TEXT, mContentText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMenuDrawer.toggleMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();

            return;
        }

        super.onBackPressed();
    }

    public void StartChat(final String[] uInfo) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        byte[] BitArray = HttpUtils.GetURLContent(uInfo[1]);
                        //        byte[] BitArray = HttpUtils.GetURLContent_img("http://img4.duitang.com/uploads/item/201207/13/20120713173422_VNZ4Q.thumb.600_0.jpeg");
//                        Bitmap HeadPic = null;

                        if (BitArray != null) {
                            HeadPic = BitmapFactory.decodeByteArray(BitArray, 0, BitArray.length);
                        }
                    }
                }
        ).start();
        String username = uInfo[0] + android.provider.Settings.Secure.getString(MainActivity.GetInstance().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        GotyeSDK.getInstance().startGotyeSDK(MainActivity.GetInstance(), username, uInfo[0], GotyeSex.NOT_SET, HeadPic, null);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isInMainView = true;
        try {
            switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                case RESULT_CANCELED:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        int ChangeState = bundle.getInt("ChangeState");


                        switch (ChangeState) {
                            case 1:   //选择用户返回
                                String sUrl = ExampleApplication.GetInstance().getString(R.string.gerenzhongxin_view) + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken");
                                obj_web.loadUrl(sUrl);
                                titleView.setText(R.string.gerenzhongxintitle);
                                p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                                break;
                            case 2:   //登陆以后的返回
                                String url = obj_web.getUrl();
                                String a[] = url.split("\\?");
                                url = url + "?UserID=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken");
                                obj_web.loadUrl(url);
                                p_PushInstance.setMode(PullToRefreshBase.Mode.DISABLED);
                                break;
                            case 3:   //普通返回不需要处理
                                String uri = obj_web.getUrl();
                                if (uri.contains("PageUserInfoData")) {//当查看完信息后返回时页面重新加载，提示消息消失
                                    obj_web.loadUrl(uri);
                                } else if (uri.contains("Guess")) {//当竞猜支持后返回竞猜列表时页面的刷新
                                    obj_web.reload();
                                }
                                break;

                        }
                    }
                    break;
                case RESULT_OK: {

                    switch (requestCode) {
                        case Constant.REQUEST_CODE_TAKE_PICTURE:
                            ContentResolver resolver = getContentResolver();
                            Uri originalUri = data.getData();        //获得图片的uri
                            Bitmap MediaPic = null;
                            try {
                                MediaPic = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                                UserPhotoUtils.UploadSubmit(MediaPic, pamars);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case Constant.REQUEST_CODE_TAKE_CAPTURE:
                            Intent intent = new Intent("com.android.camera.action.CROP");
                            intent.setDataAndType(UserPhotoUtils.GetTempFileUri(), "image/*");
                            // crop为true是设置在开启的intent中设置显示的view可以剪裁
                            intent.putExtra("crop", "true");
                            // aspectX aspectY 是宽高的比例
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                            // outputX,outputY 是剪裁图片的宽高
                            intent.putExtra("outputX", Constant.OUT_PUT_PIC_WIDTH);
                            intent.putExtra("outputY", Constant.OUT_PUT_PIC_HEGITH);
                            intent.putExtra("return-data", true);
                            intent.putExtra("noFaceDetection", true);
                            startActivityForResult(intent, Constant.REQUEST_CODE_REUSLT_PICTURE);
                            break;
                        case Constant.REQUEST_CODE_REUSLT_PICTURE:
                            Bitmap ResultPic = data.getParcelableExtra("data");
                            UserPhotoUtils.UploadSubmit(ResultPic, pamars);
                            break;
                    }
                    break;
                }
                default:
                    break;
            }


        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("ArrayIndexOutOfBoundsException", e.getLocalizedMessage());
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
                        if (Name.equalsIgnoreCase("PayInfo"))//支付宝支付
                        {
                            JSONArray array = Json.getJSONArray("Parms");
                            String Content = array.getString(0);
                            AlipayPayinfo alipay = new AlipayPayinfo();
                            alipay.AlipayPay(GetInstance(), Content);
                            break;
                        }
                        if (Name.equalsIgnoreCase("ShearContent")) {//个人中心中的邀请好友分享
                            objectParams = JsonUtils.Str2Json(Params);

                            shareCount = objectParams.getString("Title");
                            imgPath = objectParams.getString("NewsImg");
                            pathURL = objectParams.getString("Url");
                            Message MSG = new Message();
                            MSG.arg1 = 4;
                            updateHandler.sendMessage(MSG);
                            break;
                        }
                        if (Name.equalsIgnoreCase("GetDriverToken")) {//这是返回手机Token
                            TelephonyManager tm = (TelephonyManager) GetInstance().getSystemService(Context.TELEPHONY_SERVICE);
                            DeviceId = tm.getDeviceId();
                            String Url = "javascript:SetToken(" + DeviceId + ")";
                            obj_web.loadUrl(Url);
                            break;
                        }
                        if (Name.equalsIgnoreCase("MobileUserAdvice")) {//关于我们中的意见反馈
                            //意见反馈的启动
                            FeedbackAgent agent = new FeedbackAgent(MainActivity.GetInstance());
                            agent.startFeedbackActivity();
                            break;
                        }
                        if (Name.equalsIgnoreCase("ShowSelectPic")) {//个人中心的图像上传接口
                            // chooise();
                            pamars = Params + "UserId=" + LocalDataObj.GetUserLocalData("UserID") + "&Code=" + LocalDataObj.GetUserLocalData("UserToken");
                            UserPhotoUtils.StartUploadPhoto();
                            break;
                        }
                        //判断是否带参数
                        if (Name.equalsIgnoreCase("AccountManager") == true) {
                            String url = null;
                            String jsonStr = LocalDataObj.GetUserLocalData("LocalUserJson");
                            JSONArray userArray;
                            userArray = JsonUtils.Str2JsonArray(jsonStr);

                            if (userArray.length() < 2) {
                                url = ExampleApplication.GetInstance().getString(R.string.denglu_view);
                            } else {
                                String nameList = "";
                                for (int i = 0; i < userArray.length(); i++) {
                                    JSONObject user = (JSONObject) userArray.get(i);
                                    nameList += user.getString("UserNick");
//                                    try {
//                                        nameList = new String(nameList.getBytes("gb2312"), "utf-8");
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
                                    if (i != userArray.length() - 1)
                                        nameList += ",";
                                }
                                try {
                                    nameList = new String(nameList.getBytes(), "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                url = ExampleApplication.GetInstance().getString(R.string.guanlizhanghao_view) + "?nameList=" + nameList + "&UserNick=" + LocalDataObj.GetUserLocalData("UserNick");


                            }
                            if (url != null) {
                                usercountUrl = url;//多个账号选择时返回处理
                                Intent intent = new Intent(MainActivity.this, MainFragment.class);
                                intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

                                startActivityForResult(intent, 0);
//                                startActivity(intent);
                            }
                            break;
                        }
                        if (Name.equalsIgnoreCase("OnLoadFinish") == true) {
                            Message MSG = new Message();
                            MSG.arg1 = 2;
                            updateHandler.sendMessage(MSG);
                            break;
                        }
                        //跳转球队
                        if (Name.equalsIgnoreCase("JumpCampStar") == true) {
                            objectParams = JsonUtils.Str2Json(Params);
                            String url = ExampleApplication.GetInstance().getString(R.string.MainIP)
                                    + objectParams.getString("PageName")
                                    + ".aspx"
                                    + objectParams.getString("Parms")
                                    + "&UserID="
                                    + LocalDataObj.GetUserLocalData("UserID")
                                    + "&Code="
                                    + LocalDataObj.GetUserLocalData("UserToken");

                            if (url != null) {
                                Intent intent = new Intent(MainActivity.this, MainFragment.class);
                                intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);

                                intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                                intent.putExtra(MainFragment.EXTRA_FRAGMENTTITLE, objectParams.getString("Title"));

                                startActivityForResult(intent, 0);
                            }
                        }
                        //跳转页进行判断
                        if (Name.equalsIgnoreCase("JumpPage") == true) {
                            objectParams = JsonUtils.Str2Json(Params);
                            String isSelf = objectParams.getString("IsSelf");
                            //自身子菜单跳转
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

                                obj_web.loadUrl(url);
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

                                    if (url.contains("AboutPage")) {
                                        String s1[] = url.split("\\?");

                                        s1 = s1[1].split("\\&");
                                        s1 = s1[0].split("\\=");
                                        int type = Integer.parseInt(s1[1]);
                                        switch (type) {
                                            case 1://obj_web.loadUrl("http://t.qq.com/cslapp");//腾讯微博
                                            {
                                                Intent intent = new Intent(MainActivity.this, MainFragment.class);
                                                intent.putExtra(MainFragment.EXTRA_VIEW_URL, "http://t.qq.com/cslapp");
                                                intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                                                startActivityForResult(intent, 0);
                                            }
                                            break;
                                            case 2://obj_web.loadUrl("http://weibo.com/cslapp");//新浪微博
                                            {
                                                Intent intent = new Intent(MainActivity.this, MainFragment.class);
                                                intent.putExtra(MainFragment.EXTRA_VIEW_URL, "http://weibo.com/cslapp");
                                                intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);
                                                startActivityForResult(intent, 0);
                                            }
                                            break;
                                            case 3: {
                                                Intent intent = new Intent(MainActivity.this, MainFragment.class);
                                                intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);
                                                intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

                                                startActivityForResult(intent, 0);
                                            }
                                            break;
                                        }
                                        return;
                                    }
                                    Intent intent = new Intent(MainActivity.this, MainFragment.class);
                                    intent.putExtra(MainFragment.EXTRA_VIEW_URL, url);

                                    if (url.contains("SelectNews")) {
                                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_EDITPAGEWEBVIEW);
                                    } else {
                                        intent.putExtra(MainFragment.EXTRA_FRAGMENT, MainFragment.FRAGMENT_ONEPAGEWEBVIEW);

                                    }

                                    isInMainView = false;
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
