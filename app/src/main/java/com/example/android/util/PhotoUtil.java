package com.example.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoUtil {
    //打开相机
    public static String start_camera(Activity activity, int requestCode) {
        Uri imageUri;
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

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            try {
                storagePath = GetContext.getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "images";
                storageDir = new File(storagePath);
                LogUtil.e("----路径是否存在", "" + storageDir.exists());
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                outputImage = File.createTempFile(timeStamp, ".jpg", storageDir);
                LogUtil.e("-----创建的图片路径", outputImage.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageUri = FileProvider.getUriForFile(GetContext.getContext(), "com.example.android.fileprovider", outputImage);
            // set system camera Action
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            LogUtil.e("-----执行到这没1", "");
            // set save photo path
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            activity.startActivityForResult(intent, requestCode);
            return outputImage.getAbsolutePath();
//        }
        }
        LogUtil.e("-----执行到这没2", "");
        return null;
//        return outputImage;
    }

    //    打开相册
    public static void start_album(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum(activity, requestCode);
        }
        LogUtil.e("尾部直接执行么", "-----");
    }

    public static void openAlbum(Activity activity, int requestCode) {
//        Intent intent = new Intent(Intent.ACTION_PICK, null);
//        intent.setType("image/*");
//        activity.startActivityForResult(intent, requestCode);
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        /**
         * 下面这句话，与其它方式写是一样的效果，如果：
         * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         * intent.setType(""image/*");设置数据类型
         * 要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
         */
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(albumIntent, requestCode);

    }

}