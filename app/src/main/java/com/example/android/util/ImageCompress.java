package com.example.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompress {

    /**
     * 压缩图片（质量压缩）
     * @param bitmap
     */
    public static String compressImage(Bitmap bitmap,String outputpath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
        File compressimagedir = new File(GetContext.getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "compressimages");
        if (!compressimagedir.exists()) {
            compressimagedir.mkdirs();
        }
        String name = outputpath.split("/")[outputpath.split("/").length-1];
        LogUtil.e("-----划分后文件名字",name);
        File file = new File(compressimagedir,name);
        LogUtil.e("-----新创建压缩文件",":"+file);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                LogUtil.e("---压缩图片过程",e.getMessage());
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            LogUtil.e("---压缩图片过程",e.getMessage());
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file.getAbsolutePath();
    }
    public static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps==null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }
    /**
     * 图片按比例大小压缩方法
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static String getimage(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap,srcPath);// 压缩好比例大小后再进行质量压缩
    }
}
