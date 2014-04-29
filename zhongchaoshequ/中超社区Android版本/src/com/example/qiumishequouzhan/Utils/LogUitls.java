package com.example.qiumishequouzhan.Utils;

import android.util.Log;
import com.example.qiumishequouzhan.Constant;

public class LogUitls 
{
	public static final void WriteLog(String mode, String function, String params, Exception message)
	{
		if(Constant.DEBUG_MODE == true)
			Log.i("[MODE] : " + mode + ", [FUNCTION] : " + function ,  "[PARAMS] : " + params + ", [ERRORMESSAGE] : " + message.getMessage() + ", [LOCALIZEDMESSAGE] : " +  message.getLocalizedMessage());
	}
}
