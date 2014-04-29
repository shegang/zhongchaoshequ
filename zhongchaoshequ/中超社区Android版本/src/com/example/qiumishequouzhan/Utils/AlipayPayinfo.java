package com.example.qiumishequouzhan.Utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.alipay.android.app.sdk.AliPay;
import com.example.qiumishequouzhan.Constant;
import com.example.qiumishequouzhan.MainView.MainActivity;
import com.example.qiumishequouzhan.alipay.Keys;
import com.example.qiumishequouzhan.alipay.Result;
import com.example.qiumishequouzhan.alipay.Rsa;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-4
 * Time: 上午11:02
 * To change this template use File | Settings | File Templates.
 */
public class AlipayPayinfo {

    public static final String TAG = "alipay-sdk";

    private static final int RQF_PAY = 1;

    private static final int RQF_LOGIN = 2;

    public void AlipayPay(final Activity context,String Data)
    {
        String pStr = (String)Data;
        try {
            Log.i("ExternalPartner", "onItemClick");
            String info = getNewOrderInfo(pStr);
            String sign = Rsa.sign(info, Keys.PRIVATE);
            sign = URLEncoder.encode(sign);
            info += "&sign=\"" + sign + "\"&" + getSignType();
            Log.i("ExternalPartner", "start pay");
            // start the pay.
            Log.i(TAG, "info = " + info);

            final String orderInfo = info;
            new Thread() {
                public void run() {
                    AliPay alipay = new AliPay(context, mHandler);

                    //设置为沙箱模式，不设置默认为线上环境
//                    alipay.setSandBox(true);

                    String result = alipay.pay(orderInfo);

                    Log.i(TAG, "result = " + result);
                    Message msg = new Message();
                    msg.what = RQF_PAY;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();
//            Toast.makeText(ExternalPartner.this, R.string.remote_call_failed,Toast.LENGTH_SHORT).show();
        }
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Result result = new Result((String) msg.obj);

            switch (msg.what) {
                case RQF_PAY:
                {
                    int payState =  Integer.valueOf(result.GetStatePay()).intValue();
                    if (payState == 9000)
                    {
                        MainActivity.GetWebView().reload();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.GetInstance(), result.getResult(),Toast.LENGTH_SHORT).show();
                    }
                }

                    break;
                case RQF_LOGIN: {
//                    Toast.makeText(MainViewActivity.GetInstance(), result.getResult(),Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
            }
        };
    };
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
    private String getNewOrderInfo(String sID) {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append(sID);
        sb.append("\"&subject=\"");
        sb.append("摇一摇购买30次");
        sb.append("\"&body=\"");
        sb.append("摇一摇购买30次");
        sb.append("\"&total_fee=\"");
        sb.append("6.00");
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        sb.append(URLEncoder.encode(Constant.SERVER_URL+"/Charge.aspx?Charge=0"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"15d");
        sb.append("\"");

        return new String(sb);
    }
//    private String getOutTradeNo() {
//        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
//        Date date = new Date();
//        String key = format.format(date);
//
//        java.util.Random r = new java.util.Random();
//        key += r.nextInt();
//        key = key.substring(0, 15);
//        Log.d(TAG, "outTradeNo: " + key);
//        return key;
//    }

}
