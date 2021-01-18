package com.antigravitystudios.flppd;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    private static Gson gson = new Gson();

    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    public static <T> T deserialize(String string, Class<T> classOfT) {
        return gson.fromJson(string, classOfT);
    }

    public static BitmapDescriptor getBitmapDescriptor(Drawable drawable) {
        int h = drawable.getIntrinsicHeight();
        int w = drawable.getIntrinsicWidth();
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String loadStringfromAssetFile(Context context, String path) {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = context.getAssets().open(path);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateTimeString(long time) {
        SimpleDateFormat dateDateFormat = new SimpleDateFormat("hh:mm a, dd.MM.yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return dateDateFormat.format(calendar.getTime());
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
