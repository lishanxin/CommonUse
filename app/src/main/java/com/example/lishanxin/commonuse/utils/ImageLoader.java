package com.example.lishanxin.commonuse.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

public class ImageLoader {

    private static LruCache<String, Bitmap> mMemoryCache;
    private static ImageLoader mImageLoader;

    private ImageLoader(){
        //获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 32;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static ImageLoader getInstance(){
        if (mImageLoader == null)
            mImageLoader = new ImageLoader();

        return mImageLoader;
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if (getBitmapFromMemoryCache(key) == null){
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth){
        //原图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth){
            final int widthRatio = Math.round((float) width/ (float) reqWidth);
            inSampleSize = widthRatio;
        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }


}
