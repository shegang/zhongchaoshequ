package com.example.qiumishequouzhan.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class JsonUtils 
{
	/**
	 * 字符串转JSONArray类型
	 * @param JsonStr  Json字符串
	 * @return JSONArray实例，为NULL表示转换失败
	 */
	public static JSONObject Str2Json(String JsonStr)
	{
		JSONObject Json = null;
		
		if(!JsonStr.equals("")) 
		{
			try
			{
				Json = new JSONObject(JsonStr);
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
				LogUitls.WriteLog("JsonUtils", "Str2Json", JsonStr, e);
			}
		}
		
		return Json;
	}
	
	public static JSONArray Str2JsonArray(String JsonStr)
	{
		JSONArray Json = null;
		
		if(!JsonStr.equals("")) 
		{
			try
			{
				Json = new JSONArray(JsonStr);
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
				LogUitls.WriteLog("JsonUtils", "Str2JsonArray", JsonStr, e);
			}
		}
		return Json;
	}
}
