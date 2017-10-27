/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.lwh.jackknife.app.Application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @auther lwh
 */
public class ImageUtils {

    /**
     * 将Bitmap保存到jpeg图片中
     * @param bitmap
     * @param filePath
     * @throws IOException
     */
    public static void saveAsJpeg(Bitmap bitmap, String filePath) throws IOException {
        FileOutputStream fos = null;

        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 将Bitmap保存到png图片中
     * @param bitmap
     * @param filePath
     * @throws IOException
     */
    public static void saveAsPng(Bitmap bitmap, String filePath) throws IOException {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw width and height of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap compressBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options,480,800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 截取应用程序界面（去除状态栏）。
     *
     * @param activity 界面Activity。
     * @return Bitmap对象。
     */
    public static Bitmap takeScreenShot(Activity activity){
        View view =activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap,0,statusBarHeight, getScreenWH()[0], getScreenWH()[1] - statusBarHeight);
        view.destroyDrawingCache();
        return bitmap2;
    }

    /**
     * 获取屏幕的宽高，继承{@link Application}此方法才有效。
     *
     * @return 屏幕的宽度和高度。
     */
    public static int[] getScreenWH(){
        WindowManager wm = (WindowManager) Application.getInstance().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels,outMetrics.heightPixels};
    }

    /**
     * 截取应用程序界面。
     *
     * @param activity 界面Activity。
     * @return Bitmap对象。
     */
    public static Bitmap takeFullScreenShot(Activity activity){
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
        View view = activity.getWindow().getDecorView();
//		Bitmap bmp2 = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        //view.draw(new Canvas(bmp2));
        //bmp就是截取的图片了，可通过bmp.compress(CompressFormat.PNG, 100, new FileOutputStream(file));把图片保存为文件。
        //1、得到状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        System.out.println("状态栏高度：" + statusBarHeight);
        //2、得到标题栏高度
        int wintop = activity.getWindow().findViewById(android.R.id.content).getTop();
        int titleBarHeight = wintop - statusBarHeight;
        System.out.println("标题栏高度:" + titleBarHeight);
//		//把两个bitmap合到一起
//		Bitmap bmpall=Biatmap.createBitmap(width,height,Config.ARGB_8888);
//		Canvas canvas=new Canvas(bmpall);
//		canvas.drawBitmap(bmp1,x,y,paint);
//		canvas.drawBitmap(bmp2,x,y,paint);
        return bmp;
    }

    /**
     * 读取图片属性：旋转的角度。
     *
     * @param path 图片绝对路径。
     * @return degree 旋转的角度。
     * @throws IOException 输入输出异常。
     */
    public static int getPictureDegree(String path) throws IOException {
        int degree = 0;
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    /**
     * Drawable转成Bitmap。
     *
     * @param drawable 可绘制的。
     * @return 位图。
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 从资源文件中获取图片。
     *
     * @param context 上下文。
     * @param drawableId 资源文件id。
     * @return 位图。
     */
    public static Bitmap getBitmap(Context context,int drawableId){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), drawableId);
        return bmp;
    }

    /**
     * 灰白图片（去色）。
     *
     * @param bitmap 需要灰度的图片。
     * @return 去色之后的图片。
     */
    public static Bitmap toBlack(Bitmap bitmap) {
        Bitmap resultBMP = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(resultBMP);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return resultBMP;
    }

    /**
     * 将bitmap转成byte数组。
     *
     * @param bitmap 位图。
     * @return 字节数组。
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 将byte数组转成bitmap。
     *
     * @param b 字节数组。
     * @return 位图。
     */
    public static Bitmap bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 将Bitmap转换成指定大小。
     *
     * @param bitmap 需要改变大小的图片。
     * @param width 宽。
     * @param height 高。
     */
    public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }


    /**
     * 在图片右下角添加水印。
     *
     * @param srcBMP 原图。
     * @param markBMP 水印图片。
     * @return 合成水印后的图片。
     */
    public static Bitmap composeWatermark(Bitmap srcBMP, Bitmap markBMP) {
        if (srcBMP == null) {
            return null;
        }
        // 创建一个新的和SRC长度宽度一样的位图
        Bitmap newb = Bitmap.createBitmap(srcBMP.getWidth(), srcBMP.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        // 在 0，0坐标开始画入原图
        cv.drawBitmap(srcBMP, 0, 0, null);
        // 在原图的右下角画入水印
        cv.drawBitmap(markBMP, srcBMP.getWidth() - markBMP.getWidth() + 5, srcBMP.getHeight() - markBMP.getHeight() + 5, null);
        // 保存
        cv.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        cv.restore();
        return newb;
    }

    /**
     * 缩放图片。
     *
     * @param bmp 需要缩放的图片源。
     * @param newW 需要缩放成的图片宽度。
     * @param newH 需要缩放成的图片高度。
     * @return 缩放后的图片。
     */
    public static Bitmap zoom(Bitmap bmp, int newW, int newH) {
        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newW) / width;
        float scaleHeight = ((float) newH) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix,true);
        return newbm;
    }

    /**
     * 获得倒影的图片。
     *
     * @param bitmap 原始图片。
     * @return 带倒影的图片。
     */
    public static Bitmap makeReflectionImage(Bitmap bitmap){
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height/2, width, height/2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Bitmap.Config.ARGB_8888);
        Paint deafalutPaint = new Paint();
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(0, height,width,height + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,bitmap.getHeight(),0,bitmapWithReflection.getHeight()+reflectionGap,0x70ffffff,0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0,height,width,bitmapWithReflection.getHeight()+reflectionGap,paint);
        return bitmapWithReflection;
    }

    /**
     * 回收垃圾位图。
     */
    public static void recycle(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**
     * 获取指定路径下的图片的指定大小的缩略图。
     *
     * @return Bitmap 缩略图。
     */
    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度。
     *
     * @param path 图片绝对路径。
     * @return degree旋转的角度。
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }

    /**
     * 旋转图片到一定角度。
     *
     * @param angle 旋转角。
     * @param bitmap 原Bitmap图片。
     * @return 旋转后的图片。
     */
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 将图片变为圆角图片。
     *
     * @param bitmap 原Bitmap图片。
     * @param pixels 图片圆角的弧度(单位:像素(px))。
     * @return 带有圆角的图片(Bitmap 类型)。
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 将图片转化成圆形图片。
     *
     * @param bitmap 原Bitmap图片。
     * @return 圆形图片。
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        // 以下有两种方法画圆,drawRounRect和drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }
}
