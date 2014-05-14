package com.example.qiumishequouzhan.Utils;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Toast;
import com.example.qiumishequouzhan.Constant;
import com.example.qiumishequouzhan.MainView.MainActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;


public class UserPhotoUtils {



    public static Uri GetTempFileUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "wisportphoto.jpg"));
    }

    public static void StartUploadPhoto() {
         Builder p_AlertBuilder = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
            Toast toast = Toast.makeText(MainActivity.GetInstance().getApplicationContext(), "没有SD卡！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        if (p_AlertBuilder == null) {
            p_AlertBuilder = new Builder(MainActivity.GetInstance()).setTitle("设置").setItems(new String[]{"相册上传", "拍照上传"},
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = null;
                            switch (which) {
                                case 0:
                                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("image/*");
                                    intent.putExtra("crop", "true");
                                    intent.putExtra("aspectX", 1);
                                    intent.putExtra("aspectY", 1);
                                    intent.putExtra("outputX", Constant.OUT_PUT_PIC_WIDTH);
                                    intent.putExtra("outputY", Constant.OUT_PUT_PIC_HEGITH);
                                    intent.putExtra("return-data", true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    MainActivity.GetInstance().startActivityForResult(intent,Constant.REQUEST_CODE_REUSLT_PICTURE);//相册选择图片
                                    break;
                                case 1:
                                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, GetTempFileUri());
                                    MainActivity.GetInstance().startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_CAPTURE);//相机选择
                                    break;
                            }
                        }
                    }
            );
        }
        p_AlertBuilder.show();
    }

    public static void UploadSubmit(final Bitmap Pic, final String Pamars) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap nPic = ScaleBitmap(Pic);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            nPic.compress(Bitmap.CompressFormat.JPEG, Constant.BITMAP_DEFAULT_QUALITY, baos);
                            byte[] byteArray = baos.toByteArray();

                            //JSONObject jsonObject = JsonUtils.Str2Json(Pamars);
                            String str[] = Pamars.split("\\?");
                            str = str[1].split("\\&");
                         String users[] = str[0].split("\\=");
                           String codes[] =str[1].split("\\=");
                            int UserID = Integer.parseInt(users[1]);
                            String UserCode = codes[1];

                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(Constant.SERVER_URL + Constant.SERVER_UPLOADPIC_PATH);
                            MultipartEntity entity = new MultipartEntity();

                            entity.addPart("UserId", new StringBody(Integer.toString(UserID)));
                            entity.addPart("Code", new StringBody(UserCode));
                            entity.addPart("file", new ByteArrayBody(byteArray, "Android.jpg"));
                            post.setEntity(entity);

                            HttpResponse response;
                            StringBuffer sb = new StringBuffer();

                            response = httpClient.execute(post);
                            int stateCode = response.getStatusLine().getStatusCode();

                            if (stateCode == HttpStatus.SC_OK) {
                                HttpEntity result = response.getEntity();
                                if (result != null) {
                                    InputStream is = result.getContent();
                                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                    String tempLine;
                                    while ((tempLine = br.readLine()) != null) {
                                        sb.append(tempLine);
                                    }
                                }
                            }
                            post.abort();
                            String[] Result = sb.toString().split("#");

                            if (Result.length == 2) {
                                String Path =  Result[1];
                               // HttpUtils.GetURLContent_img(Path);
                                MainActivity.GetInstance().SendCallBack(Path);//这里是回调函数


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }

    private static Bitmap ScaleBitmap(Bitmap Pic) {
        int imgWidth = Pic.getWidth();
        int imgHeight = Pic.getHeight();


        if (imgWidth > Constant.BITMAP_MAX_WIDTH || imgHeight > Constant.BITMAP_MAX_HEIGHT) {
            float scale;

            float scaleWidth = ((float) Constant.BITMAP_MAX_WIDTH) / imgWidth;
            float scaleHeight = ((float) Constant.BITMAP_MAX_HEIGHT) / imgHeight;
            if (scaleWidth > scaleHeight) {
                scale = scaleWidth;
            } else {
                scale = scaleHeight;
            }

            // 取得想要缩放的matrix参数���
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            // 得到新的图片
            Pic = Bitmap.createBitmap(Pic, 0, 0, imgWidth, imgHeight, matrix, true);
        }
        return Pic;
    }
}
