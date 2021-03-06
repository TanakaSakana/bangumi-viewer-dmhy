package com.BangumiList.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SDUtil {
    public static Bitmap getSDImg(String key, Context context) {
        Bitmap bitmap;
        String HashKey = md5(key);
        try {
            BufferedInputStream in = new BufferedInputStream(context.openFileInput(HashKey));
            bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean saveSDImg(Bitmap img, String key, Context context) {
        if (img == null)
            return false;
        String HashKey = md5(key);
        try {
            BufferedOutputStream out = new BufferedOutputStream(context.openFileOutput(HashKey, Context.MODE_PRIVATE));
            img.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
