package com.example.qiumishequouzhan.Utils;

import java.io.ByteArrayInputStream;

import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.qiumishequouzhan.Constant;


import com.example.qiumishequouzhan.LocalDataObj;
import com.example.qiumishequouzhan.R;
import com.example.qiumishequouzhan.webviewpage.WelcomPage;

public class Splash 
{
	private static LinearLayout p_Instance;
	
	private static final String SPLASH_PATH = "Splash";
	private static final String SPLASH_NAME = "Splash.jpg";
	private static final String SPLASH_KEY = "SPLASH_VERSION";
	
	/*public static void DisplaySplash()
	{
		ThreadDownLoadSplash();

      //  MainActivity.SPLASH_OVERED = false;
		
		if(p_Instance == null)
		{
			p_Instance  = new LinearLayout(WelcomPage.GetInstance());
			p_Instance.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			p_Instance.setOrientation(LinearLayout.VERTICAL);
			p_Instance.setGravity(Gravity.CENTER);
            ImageView imageView = (ImageView) p_Instance.findViewById(R.id.img_qidong);
			
			Drawable Splash = GetSplash();
			
			if(Splash == null)
			//	p_Instance.setBackgroundResource(R.drawable.qidong);
              //  Rightbutton.setImageDrawable(getResources().getDrawable(R.drawable.caifubang));
            imageView.setImageDrawable(WelcomPage.GetInstance().getResources().getDrawable(R.drawable.qidong));
			else
				//p_Instance.setBackgroundDrawable(Splash);
            imageView.setImageDrawable(Splash);
			

//            MainActivity.GetLayOutView().addView(p_Instance);
//
//			MessageFactory.SendMessageDaely(MessageConstant.START_APP, Constant.SPLAH_DELAY);
		}
	}*/
	
	public static void ThreadDownLoadSplash()
	{
		new Thread(
				new Runnable()
				{
					@Override
		            public void run() 
					{
						String PorxyURL = Constant.SERVER_URL + Constant.SERVER_SPLASH_PORXY+"?type=0";
						String Version = FileUtils.Bytes2String(HttpUtils.GetURLContent(PorxyURL + "?Work=GetVersion"));
						
						String LocalVersion = LocalDataObj.GetUserLocalData(SPLASH_KEY);

						if(LocalVersion.equals("") == true)
							LocalVersion = "0";

						if(LocalVersion.equals(Version) == false)
						{
							String Path = FileUtils.Bytes2String(HttpUtils.GetURLContent(PorxyURL));
							
							if(Path.equals("") == false)
							{
								byte[] Buffer = HttpUtils.GetURLContent(Constant.SERVER_URL + Path);
								
								if(Buffer == null || Buffer.length == 0)
									return;
								
								ByteArrayInputStream Stream = new ByteArrayInputStream (Buffer);
								FileUtils.WriteFile2Store(SPLASH_PATH, SPLASH_NAME, Stream);

                                LocalDataObj.SetUserLocalData(SPLASH_KEY, Version);
							}
							
						}

					}

				}).start();

	}
	
	@SuppressWarnings("deprecation")
	public static Drawable GetSplash()
	{
		Drawable Result = null;
		
		Bitmap file =  FileUtils.ReadFile2Bitmap(SPLASH_PATH, SPLASH_NAME);
		
		if(file != null)
		{
			Result = new BitmapDrawable(file);
		}
		
		return Result;
	}
	
	/*public static void RemoveSplash()
	{
		if(p_Instance != null)
		{
			
			p_Instance.removeAllViews();
            MainActivity.GetLayOutView().removeView(p_Instance);
			p_Instance.destroyDrawingCache();
			p_Instance = null;
            MainActivity.GetInstance().ChangeWebView();
			//MainActivity.SPLASH_OVERED = true;
		}
	}*/
}
