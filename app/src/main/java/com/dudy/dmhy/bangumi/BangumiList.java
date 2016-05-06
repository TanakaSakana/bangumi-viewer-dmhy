package com.dudy.dmhy.bangumi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dudy.dmhy.SDUtil;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BangumiList implements Serializable {

    public static ArrayList<Bangumi> bangumiList = new ArrayList<>();
    public static HashMap<String, Bitmap> bitmapList = new HashMap<>();
    public static ExecutorService executorService;

    public BangumiList(List<Bangumi> list) {
        bangumiList.clear();
        bangumiList.addAll(list);
    }

    public static HashMap<String, Bitmap> getBitmapList() {
        return bitmapList;
    }

    public static Bitmap getBangumiBitmap(String name, Context context) {
        String NameHash = SDUtil.md5(name);
        Bitmap result = null;
        if (bitmapList.containsKey(NameHash)) {
            result = bitmapList.get(NameHash);
        } else if (SDUtil.getSDImg(NameHash, context) != null) {
            result = SDUtil.getSDImg(NameHash, context);
        }
        return result;
    }

    public static void renderImage(Activity mContext) {
        executorService = Executors.newCachedThreadPool();
        for (Bangumi item : bangumiList) {
            String NameHash = SDUtil.md5(item.getName());
            if (!bitmapList.containsKey(NameHash) || bitmapList.get(NameHash) == null) {
                Bitmap SDTemp = SDUtil.getSDImg(NameHash, mContext);
                if (SDTemp != null) {
                    bitmapList.put(NameHash, SDTemp);
                } else {
                    Bitmap img = getImg(item.getImage());
                    if (img != null) {
                        bitmapList.put(NameHash, img);
                        SDUtil.saveSDImg(img, NameHash, mContext);
                    }
                }
            }
        }
        executorService.shutdown();
    }

    public static Bitmap getImg(final String path) {
        Future<Bitmap> future = executorService.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap bitmap = null;
                HttpURLConnection connection;
                try {
                    URL url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    connection.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
        });
        Bitmap img = null;
        try {
            img = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }
}
