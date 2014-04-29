package com.example.qiumishequouzhan.Utils;




import com.example.qiumishequouzhan.Constant;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils 
{
	/**
	 * 从一个URL上面读取内容
	 * @param URL
	 */
	private static byte[] GetURLContent(String URL, String Method, String Params, String ContentType, String CharSet)
	{
		byte[] Buffer = null;
		
		if(Params == null)
			Params = "";
		
		try 
		{
			Method = Method.toUpperCase();
			
			URL url = new URL(URL);
            // 使用HttpURLConnection打开连接  
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection(); 

            if(Method.equals("POST") == true)
            {
            	//因为这个是post请求,设立需要设置为true  
                urlConn.setDoOutput(true);  
                urlConn.setDoInput(true);  
                // Post 请求不能使用缓存  
                urlConn.setUseCaches(false);  
                urlConn.setInstanceFollowRedirects(true);  
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的  
                urlConn.setRequestProperty("Content-Type",ContentType);  
              //设置编码格式
                urlConn.setRequestProperty("Accept-Charset", CharSet);
            }
            // 设置Method
            urlConn.setRequestMethod(Method);  

            //设置超时
            urlConn.setConnectTimeout(50 * 1000);  
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，  
            // 要注意的是connection.getOutputStream会隐含的进行connect。  
            urlConn.connect(); 
            
            if(Method.equals("POST") == true)
            {
            	byte[] sBuffer = Params.getBytes(CharSet);
            	
	            //DataOutputStream流  
	            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());  
	            //将要上传的内容写入流中  
	            out.write(sBuffer, 0, sBuffer.length);
	            //刷新、关闭  
	            out.flush();  
	            out.close(); 
            }
            
            int ResponseCode = urlConn.getResponseCode();
            
            if (ResponseCode == 200) 
            {  
            	final InputStream inputStream = urlConn.getInputStream();      

                final ByteArrayOutputStream ByteArray = new ByteArrayOutputStream();         

                final byte[] b = new byte[256];

                int rv = 0;

                while ( ( rv = inputStream.read(b) ) > 0 ) ByteArray.write(b, 0, rv);
                
                Buffer = ByteArray.toByteArray();
                
                ByteArray.close();
                inputStream.close();
            }
            
            urlConn.disconnect(); 
		} 
		catch (Exception e) 
		{
			LogUitls.WriteLog("HttpUtils", "GetURLContent", URL, e);
		} 
		
		return Buffer;
	}
	/**
	 * 以Get方式获取服务器返回数据
	 * @param URL 服务器地址
	 * @return
	 */
	public static byte[] GetURLContent(String URL)
	{
		return GetURLContent(URL, "GET", "", "text/html", Constant.DEFAULT_CHARSET);
	}
	
	public static byte[] GetWebServiceJsonContent(String URL, String Params)
	{
		return GetURLContent(URL, "POST", Params, "application/json;utf-8", Constant.DEFAULT_CHARSET);

	}
    public static byte[] GetURLContent_img(String URL)
    {
        byte[] Buffer = null;


            final InputStream inputStream = getInputStream(URL);

            final ByteArrayOutputStream ByteArray = new ByteArrayOutputStream();
        try
        {
            final byte[] b = new byte[256];

            int rv = 0;

            while ( ( rv = inputStream.read(b) ) > 0 ) ByteArray.write(b, 0, rv);

            Buffer = ByteArray.toByteArray();

            ByteArray.close();
            inputStream.close();
        }
        catch (Exception e)
        {
            LogUitls.WriteLog("HttpUtils", "GetURLContent", URL, e);
        }
        return Buffer;
    }
    public static InputStream getInputStream(String path)
    {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try
        {
            URL url = new URL(path);
            if (null != url)
            {
                httpURLConnection = (HttpURLConnection) url.openConnection();

                // 设置连接网络的超时时间
                httpURLConnection.setConnectTimeout(5000);

                // 打开输入流
                httpURLConnection.setDoInput(true);

                // 设置本次Http请求使用的方法
                httpURLConnection.setRequestMethod("GET");

                if (200 == httpURLConnection.getResponseCode())
                {
                    // 从服务器获得一个输入流
                    inputStream = httpURLConnection.getInputStream();

                }

            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return inputStream;
    }
}
