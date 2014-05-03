package com.example.qiumishequouzhan.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


import android.app.Activity;
import android.content.Context;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.qiumishequouzhan.R;
import com.example.qiumishequouzhan.Constant;
import com.example.qiumishequouzhan.LocalDataObj;
import com.example.qiumishequouzhan.MainView.MainActivity;

import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMRichMedia;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UMengUtils {
    /**
     * 用户纯授权
     */
    public static Context m_context;

    public static void Empower(final SHARE_MEDIA MediaName, final WebView webView) {
        UMSocialService controller = UMServiceFactory.getUMSocialService(MainActivity.GetInstance().getLocalClassName(), RequestType.SOCIAL);
        controller.doOauthVerify(MainActivity.GetInstance(), MediaName, new UMAuthListener() {
            @Override
            public void onCancel(SHARE_MEDIA arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA arg1) {
                // TODO Auto-generated method stub
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    UMSocialService controller = UMServiceFactory.getUMSocialService(MainActivity.GetInstance().getLocalClassName(), RequestType.SOCIAL);
                    controller.getPlatformInfo(MainActivity.GetInstance(), MediaName,
                            new UMDataListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int status, Map<String, Object> info) {
                                    //相关平台的授权信息都以K-V的形式封装在info中
                                    // webView.loadUrl();
                                    //Commond.SetResult("1");
                                    //Commond.SendCallBack();
                                }
                            });
                } else {
                    //  Toast.makeText(MainViewActivity.GetInstance(), "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(SocializeException arg0,
                                SHARE_MEDIA arg1) {
                // TODO Auto-generated method stub
                //Toast.makeText(MainViewActivity.GetInstance(), "出错了.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart(SHARE_MEDIA arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(MainActivity.GetInstance(), "开始授权.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 用户授权部分
     */
    public static void SSOLogin(final SHARE_MEDIA MediaName, final Handler updateHandle, final Activity context) {
        UMSocialService controller = UMServiceFactory.getUMSocialService(context.getLocalClassName().getClass().getName(), RequestType.SOCIAL);

        controller.doOauthVerify(context, MediaName, new UMAuthListener() {
            @Override
            public void onCancel(SHARE_MEDIA arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA arg1) {
                // TODO Auto-generated method stub
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    UMSocialService controller = UMServiceFactory.getUMSocialService(MainActivity.class.getName(), RequestType.SOCIAL);
                    controller.getPlatformInfo(MainActivity.GetInstance(), MediaName,
                            new UMDataListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int status, Map<String, Object> info) {
                                    //相关平台的授权信息都以K-V的形式封装在info中
                                    if (status == 200 && info != null) {

                                        String UID = "";
                                        String Usernick = "";
                                        int UserSex = 0;
                                        int OAuthType = 0;
                                        Integer gender = null;

                                        switch (MediaName) {
                                            case SINA:

                                                UID = info.get("uid").toString();
                                                Usernick = info.get("screen_name").toString();
                                                gender = Integer.valueOf(info.get("gender").toString()).intValue();
                                                UserSex = gender.intValue();
                                                OAuthType = 1;
                                                break;
                                            case TENCENT:
                                                UID = info.get("uid").toString();
                                                Usernick = info.get("screen_name").toString();
                                                gender = Integer.valueOf(info.get("gender").toString()).intValue();
                                                UserSex = gender.intValue();
                                                OAuthType = 2;
                                                break;
                                            default:
                                                return;
                                        }

                                        AutoPostShare(MediaName);
                                        if (UserSex != 1) UserSex = 0;
                                        //[NSString stringWithFormat:@"%d%@%@%d",1,qqAccount.usid,qqAccount.userName,0]   JustDoIt~Test
//                                        NSString*Code=[Tools md5:[NSString stringWithFormat:@"%d%@%@%d",1,qqAccount.usid,qqAccount.userName,0]];
//                                        Code=[Tools md5:[NSString stringWithFormat:@"%@%@",Code,md5Key]];
                                        String s = OAuthType + UID + Usernick + UserSex;
                                        String str1 = MD5("123123");//4297f44b13955235245b2497399d7a93
                                        String str2 = MD5("111111");//96e79218965eb72c92a549dd5a330112
                                        final String Code = MD5(s);//0f3b6cfa26ccb96ef565a726183e1743JustDoIt~Test
                                        final String Code1 = MD5(Code + "JustDoIt~Test");//e8655b28359b5e888f651a842618018b
                                        final String uid = UID;
                                        final String usernick = Usernick;
                                        final int userSex = UserSex;
                                        final int oauthType = OAuthType;
                                        new Thread() {
                                            @Override
                                            public void run() {
//你要执行的方法
//执行完毕后给handler发送一个空消息
                                                String Url = context.getString(R.string.serverurl) + "/OAuthLogin";
                                                byte[] date = HttpUtils.GetWebServiceJsonContent(Url,
                                                        "{\"oname\":" + oauthType + " ,\"uid\":\"" + uid + "\",\"Usernick\":\"" + usernick + "\", \"UserSex\":" + userSex + ", \"Code\":\"" + Code1 + "\"}");
                                                String Result = FileUtils.Bytes2String(date);
                                                JSONObject Json = JsonUtils.Str2Json(Result);
                                                try {
                                                    Json = Json.getJSONObject("d");
                                                    Json = Json.getJSONObject("Data");
                                                    String Code = Json.getString("Code");
                                                    String uid = Json.getString("UserId");
                                                    String UserName = Json.getString("UserName");
                                                    String UserNick = Json.getString("UserNick");
                                                    String UserSex = Json.getString("UserSex");
                                                    String UserHeadImg = Json.getString("UserHeadImg");

                                                    LocalDataObj.SetUserLocalData("UserName", UserName);
                                                    LocalDataObj.SetUserLocalData("UserToken", Code);
                                                    LocalDataObj.SetUserLocalData("UserID", uid);
                                                    LocalDataObj.SetUserLocalData("UserNick", UserNick);
                                                    LocalDataObj.SetUserLocalData("UserSex", UserSex);
                                                    LocalDataObj.SetUserLocalData("UserHeadImg", UserHeadImg);
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
                                                context.setResult(Activity.RESULT_CANCELED, context.getIntent().putExtras(bundle));

                                                Message MSG = new Message();
                                                MSG.arg1 = 1;
                                                updateHandle.sendMessage(MSG);
                                            }
                                        }.start();


                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.GetInstance(), "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            public String MD5(String string) {
                byte[] hash;
                try {
                    hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("Huh, MD5 should be supported?", e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Huh, UTF-8 should be supported?", e);
                }

                StringBuilder hex = new StringBuilder(hash.length * 2);
                for (byte b : hash) {
                    if ((b & 0xFF) < 0x10) hex.append("0");
                    hex.append(Integer.toHexString(b & 0xFF));
                }
                return hex.toString();
            }


            @Override
            public void onError(SocializeException arg0,
                                SHARE_MEDIA arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.GetInstance(), "出错了.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart(SHARE_MEDIA arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(MainActivity.GetInstance(), "开始授权.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void InitUMengConfig(Context context) {
        m_context = context;
        UMSocialService controller = UMServiceFactory.getUMSocialService(m_context.getClass().getName(), RequestType.SOCIAL);
        UMWXHandler.WX_APPID = Constant.APP_WEIXIN_ID;//设置微信的Appid
        UMWXHandler.WX_CONTENT_TITLE = Constant.APP_TITLE;
        UMWXHandler.WXCIRCLE_CONTENT_TITLE = Constant.APP_TITLE;
        UMWXHandler.CONTENT_URL = Constant.APP_DOWNLOAD_PATH; //微信图文分享必须设置一个url

        SocializeConfig config = controller.getConfig();
        //设置支持的Web平台
        config.setPlatforms(SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA);
        //关闭短信分享，邮件分享功能，
        config.setShareSms(false);
        config.setShareMail(false);
        //添加微信平台
        config.supportWXPlatform(m_context);
        //添加微信朋友圈
        config.supportWXPlatform(m_context, UMServiceFactory.getUMWXHandler(MainActivity.GetInstance()).setToCircle(true));
        controller.openShare(context, false);
    }

    /**
     * 微信内容分享
     *
     * @param Content
     */
    public static void ShareContent(String Content) {
        ShareContent(Content, null, null);
    }

    public static void ShareContent(String Content, final String URL, final String weburl) {
        if (Content.length() > 80) {
            Content = Content.substring(0, 80);
            Content = Content + "......";
        }
        if (weburl != null) {
            UMWXHandler.CONTENT_URL = weburl;
        }

        Content = Content + "[分享自@中超社区] 阅读全文：" + UMWXHandler.CONTENT_URL;

        final UMSocialService controller = UMServiceFactory.getUMSocialService(m_context.getClass().getName(), RequestType.SOCIAL);
        controller.setShareContent(Content);

        if (URL == null || URL.equals("") == true) {
            controller.setShareMedia(new UMImage(MainActivity.GetInstance(), BitmapFactory.decodeResource(MainActivity.GetInstance().getResources(), R.drawable.ic_launcher)));//设置分享图片内容
        } else {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            byte[] BitArray = HttpUtils.GetURLContent(URL);

                            Bitmap URLPIC = null;

                            if (BitArray != null) {
                                URLPIC = BitmapFactory.decodeByteArray(BitArray, 0, BitArray.length);
                            }

                            controller.setShareMedia(new UMImage(MainActivity.GetInstance(), URLPIC));//设置分享图片内容
                        }
                    }).start();
        }
    }

    public static void UserAdvice() {
        FeedbackAgent agent = new FeedbackAgent(MainActivity.GetInstance());
        agent.startFeedbackActivity();
    }

    //获取授权状态
    public static int GetAuthState(SHARE_MEDIA Media) {
        if (UMInfoAgent.isOauthed(MainActivity.GetInstance(), Media) == true)
            return 1;
        return 0;
    }

    public static void PostShare(String shareContent, SHARE_MEDIA Media) {
        //构建分享内容
        UMShareMsg shareMsg = new UMShareMsg();


        if (shareContent.length() > 50) {
            shareContent = shareContent.substring(0, 50);
            shareContent = shareContent + "......";
        }

        shareContent = shareContent + "[分享自@中超社区] 阅读全文：" + Constant.APP_DOWNLOAD_PATH;

        //设置分享文字
        shareMsg.text = shareContent;

        PostShare(shareMsg, Media);
    }

    private static void AutoPostShare(final SHARE_MEDIA MediaName) {

        //构建分享内容
        UMShareMsg shareMsg = new UMShareMsg();
        //设置分享文字
        shareMsg.text = Constant.AUTO_SHARE_CONTENT;
        //设置分享图片
        //new UMImage(MainActivity.GetInstance(),BitmapFactory.decodeResource(MainActivity.GetInstance().getResources(), R.drawable.weibo_send))
        //shareMsg.setMediaData(new UMRichMedia(Constant.SERVER_URL + Constant.AUTO_SHARE_PICPATH, MediaType.IMAGE));
        shareMsg.setMediaData(UMRichMedia.toUMRichMedia(new UMImage(MainActivity.GetInstance(), BitmapFactory.decodeResource(MainActivity.GetInstance().getResources(), R.drawable.weibo_send))));

        switch (MediaName) {
            case SINA:
                PostShare(shareMsg, SHARE_MEDIA.SINA);
                break;
            case TENCENT:
                PostShare(shareMsg, SHARE_MEDIA.TENCENT);
                break;
            default:
                return;
        }
    }

    private static void PostShare(UMShareMsg shareMsg, SHARE_MEDIA Media) {
        UMSocialService controller = UMServiceFactory.getUMSocialService(MainActivity.class.getName(), RequestType.SOCIAL);


        controller.postShare(MainActivity.GetInstance(), Media, shareMsg, new SnsPostListener() {
            @Override
            public void onStart() {
                //Toast.makeText(MainActivity.GetInstance(), "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    //Toast.makeText(MainActivity.GetInstance(), "分享成功.", Toast.LENGTH_SHORT).show();
                } else {
                    //String eMsg = "";
                    //if (eCode == -101) eMsg = "没有授权";

                    // Toast.makeText(MainActivity.GetInstance(), "分享失败[" + eCode + "] " + eMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
