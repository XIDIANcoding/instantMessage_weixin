package com.avoscloud.chat.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

  public static void toast(Context context, String str) {
    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
  }

  public static void showInfoDialog(Activity cxt, String msg, String title) {
    AlertDialog.Builder builder = getBaseDialogBuilder(cxt);
    builder.setMessage(msg)
        .setPositiveButton(cxt.getString(R.string.chat_utils_right), null)
        .setTitle(title)
        .show();
  }

  public static AlertDialog.Builder getBaseDialogBuilder(Activity ctx) {
    return new AlertDialog.Builder(ctx).setTitle(R.string.chat_utils_tips).setIcon(R.drawable.utils_icon_info_2);
  }

  public static void fixAsyncTaskBug() {
    // android bug
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected Void doInBackground(Void... params) {
        return null;
      }
    }.execute();
  }

  public static void toast(int id) {
    toast(App.ctx, id);
  }

  public static void toast(String s) {
    toast(App.ctx, s);
  }

  public static void toast(Context cxt, int id) {
    Toast.makeText(cxt, id, Toast.LENGTH_SHORT).show();
  }

  public static boolean doubleEqual(double a, double b) {
    return Math.abs(a - b) < 1E-8;
  }

  public static String getPrettyDistance(double distance) {
    if (distance < 1000) {
      int metres = (int) distance;
      return String.valueOf(metres) + App.ctx.getString(R.string.discover_metres);
    } else {
      String num = String.format("%.1f", distance / 1000);
      return num + App.ctx.getString(R.string.utils_kilometres);
    }
  }

  public static ProgressDialog showSpinnerDialog(Activity activity) {
    //activity = modifyDialogContext(activity);
    ProgressDialog dialog = new ProgressDialog(activity);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCancelable(true);
    dialog.setMessage(App.ctx.getString(R.string.chat_utils_hardLoading));
    if (!activity.isFinishing()) {
      dialog.show();
    }
    return dialog;
  }

  public static boolean filterException(Exception e) {
    if (e != null) {
      toast(e.getMessage());
      return false;
    } else {
      return true;
    }
  }

  public static void saveBitmap(String filePath, Bitmap bitmap) {
    File file = new File(filePath);
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(file);
      if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
        out.flush();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        out.close();
      } catch (Exception e) {
      }
    }
  }
}
