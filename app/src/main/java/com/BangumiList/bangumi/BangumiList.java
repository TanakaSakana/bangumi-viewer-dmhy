package com.BangumiList.bangumi;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.BangumiList.GloVar.BangumiData;
import com.BangumiList.Util.SDUtil;

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

    // This list is use to render bitmap

    private static HashMap<String, Bitmap> bitmapList = new HashMap<>();
    private static ExecutorService executorService;
    private ArrayList<Bangumi> bangumiList = new ArrayList<>();

    public BangumiList(List<Bangumi> list) {
        bangumiList.clear();
        bangumiList.addAll(list);
    }

    public static Bitmap getBangumiBitmap(String Name, Context context) {
        if (bitmapList.containsKey(Name))
            return bitmapList.get(Name);
        else if (SDUtil.getSDImg(Name, context) != null)
            return SDUtil.getSDImg(Name, context);
        else
            return null;
    }

    // Image archival strategy
    public void ImageArchiving(Context mContext, final ProgressDialog mDialog) {
        mDialog.setTitle(BangumiData.PROGRESS_IMAGE_ARCHIVING);
        mDialog.setMax(bangumiList.size());
        executorService = Executors.newCachedThreadPool();
        for (Bangumi item : bangumiList) {
            String Name = item.getName();
            Bitmap BitmapTemp;
            if (!bitmapList.containsKey(Name) || bitmapList.get(Name) == null) {
                if ((BitmapTemp = SDUtil.getSDImg(Name, mContext)) != null)
                    bitmapList.put(Name, BitmapTemp);
                else if ((BitmapTemp = getImgFromHTTP(item.getImageLink())) != null) {
                    SDUtil.saveSDImg(BitmapTemp, Name, mContext);
                    bitmapList.put(Name, BitmapTemp);
                }
            }
            mDialog.incrementProgressBy(1);
        }
        executorService.shutdown();
    }

    // Get image from internet
    public static Bitmap getImgFromHTTP(final String path) {
        Future<Bitmap> future = executorService.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                HttpURLConnection connection;
                try{
                    URL url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    connection.getInputStream().close();
                    return bitmap;
                } catch (Exception e) {
                    return null;
                }
            }
        });
        try{
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }
}
