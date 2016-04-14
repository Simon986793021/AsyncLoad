package com.alpha.imooc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * @author Simon 图片加载类
 */
public class ImageLoader {
    private ImageView imageView;
    private String myurl;
    private final LruCache<String, Bitmap> myLruCache;

    public ImageLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取最大内存
        int cacheSize = maxMemory / 4;
        myLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @SuppressLint("NewApi")
            /*
             * 在每次存入缓存的时候调用
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();

            }
        };
    }

    /*
     * 增加到缓存
     */
    public void setBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            myLruCache.put(url, bitmap);
        }
    }

    /*
     * 从缓存中获取数据
     */
    public Bitmap getBitmapFromCache(String url) {
        return myLruCache.get(url);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (imageView.getTag().equals(myurl)) {
                imageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    /*
     * 通过多线程加载图片 Thread+Handler
     */
    public void showImage(ImageView imageView, final String url) {
        this.imageView = imageView;
        myurl = url;
        new Thread() {
            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                Bitmap bitmap = getBitmapFromURL(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }.start();
    }

    public Bitmap getBitmapFromURL(String urlString) {
        Bitmap bitmap;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url
                        .openConnection();
                inputStream = new BufferedInputStream(
                        httpURLConnection.getInputStream());
                bitmap = BitmapFactory.decodeStream(inputStream);
                httpURLConnection.disconnect();// 释放资源
                return bitmap;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     * 通过AsyncTask加载图片
     */

    public void showImageByAsyncTask(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);// 从缓存中获取图片
        if (bitmap == null) {
            // 如果没有，就去网络中下载
            new NewsAsyncTask(imageView, url).execute(url);
        } else {
            // 有的话，直接设置
            imageView.setImageBitmap(bitmap);

        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView myImageView;
        private final String myUrl;

        public NewsAsyncTask(ImageView imageView, String url) {
            myImageView = imageView;
            myUrl = url;
        }

        /*
         * 必须重写的方法，在子线程中执行。
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = params[0];
            Bitmap bitmap = getBitmapFromURL(params[0]);
            /*
             * 将下载的图片加入缓存
             */
            if (bitmap != null) {
                setBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (myImageView.getTag().equals(myUrl)) {
                myImageView.setImageBitmap(result);
            }

        }
    }
}
