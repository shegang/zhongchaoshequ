package com.example.qiumishequouzhan.Utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.qiumishequouzhan.ExampleApplication;
import com.example.qiumishequouzhan.MainView.MainActivity;

import java.io.*;


public class FileUtils
{

    public static String Bytes2String(byte[] Buffer)
    {
        return Bytes2String(Buffer, "UTF-8");
    }
	public static String Bytes2String(byte[] Buffer, String CharSet)
	{
		String Result = "";
		
		if(Buffer != null)
		{
			try 
			{
				Result =  new String(Buffer, CharSet);
			} 
			catch (UnsupportedEncodingException e) {
				
				LogUitls.WriteLog("FileUtils", "Bytes2String", "{Buffer}", e);
			}
		}
		return Result;
	}
    public static File WriteFile2Store(String path,String fileName,InputStream inputStream)
    {
        File file = null;
        OutputStream output=null;
        String FileName = path + File.separator + fileName;
        try
        {
            CreateDir(path);
            file = CreateFile(FileName);
            if(file != null)
            {
                output = new FileOutputStream(file);

                int length = inputStream.available();
                byte buffer[] = new byte[length];

                int readCount = 0;
                while (readCount < length)
                {
                    readCount += inputStream.read(buffer, readCount, length - readCount);
                }
                output.write(buffer);
                output.flush();
                output.close();
                output = null;
            }
        }
        catch (Exception e)
        {
            LogUitls.WriteLog("FileUtils", "WriteFile2Store", FileName, e);
        }
        return file;
    }
    private static String GetStorePath()
    {
        return ExampleApplication.GetInstance().getFilesDir().getPath();
    }
    public static String GetFileRealPath(String FileName)
    {
        String realPath = GetStorePath() + File.separator + FileName;
        return realPath;
    }
    public static Bitmap ReadFile2Bitmap(String path,String fileName)
    {
        String Path = path + File.separator + fileName;
        String RealPath = GetFileRealPath(Path);

        Bitmap Result = null;

        if(IsFileExist(Path)== true)
        {
            try
            {
                FileInputStream inStream = new FileInputStream(RealPath);
                Result = BitmapFactory.decodeStream(inStream);
                inStream.close();
                inStream = null;
            }
            catch (Exception e)
            {
                LogUitls.WriteLog("FileUtils", "ReadFile2String", Path, e);
            }
        }
        return Result;
    }
    private static File CreateDir(String dirName)
    {
        String CreatPath = GetStorePath() + File.separator + dirName;

        File dir=new File(CreatPath);

        if (!dir.exists())
        {
            dir.mkdir();
        }
        return dir;
    }
    private static File CreateFile(String fileName) throws IOException
    {
        String Path = GetStorePath() + File.separator + fileName;
        File file = new File(Path);
        file.createNewFile();
        return file;
    }
    public static Boolean IsFileExist(String fileName)
    {
        String Path = GetStorePath() + File.separator + fileName;
        File file = new File(Path);
        return file.exists();
    }
}
