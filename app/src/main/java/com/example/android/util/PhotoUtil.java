package com.example.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoUtil {

    /**
     * 开始拍照
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static String start_camera(Activity activity, int requestCode) {
        String serverimagepath;
        Uri imageUri = null;
        String storagePath;
        File storageDir;
        File outputImage = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//之后再弄其他目录下的照片
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 2);
////        } else {
            // save image in cache path
//        outputImage = new File(GetContext.getContext().getExternalCacheDir(),System.currentTimeMillis() + ".jpg");
//            outputImage = new File(Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                    .getAbsolutePath()+File.separator + "camerademo",System.currentTimeMillis() + ".jpg");
//            LogUtil.e("----保存的图片路径", outputImage.getAbsolutePath());
//            try {
//                if (outputImage.exists()) {
//                    outputImage.delete();
//                }
//                outputImage.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (Build.VERSION.SDK_INT >= 24) {
//                LogUtil.e("-----执行到这没2", "");
//                // compatible with Android 7.0 or over
//                imageUri = FileProvider.getUriForFile(activity, "com.example.android.fileprovider", outputImage);
//            } else {
//                LogUtil.e("-----执行到这没3", "");
//                imageUri = Uri.fromFile(outputImage);
//            }
        try {

            storagePath = GetContext.getContext().getExternalCacheDir().getAbsolutePath() + File.separator +"images";
            storageDir = new File(storagePath);
            storageDir.mkdirs();
            outputImage = File.createTempFile(timeStamp,".jpg",storageDir);
            LogUtil.e("-----创建的图片路径",outputImage.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageUri = FileProvider.getUriForFile(GetContext.getContext(),"com.example.android.fileprovider",outputImage);
        // set system camera Action
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            LogUtil.e("-----执行到这没1", "");
            // set save photo path
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            activity.startActivityForResult(intent, requestCode);
//        }
        return outputImage.getAbsolutePath();
//        return outputImage;
    }


    /**
     * 相册选择
     *
     * @param activity
     * @param requestCode
     */
    public static void start_album(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum(activity, requestCode);
        }

    }

    public static void openAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

}