package com.avoscloud.chat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.avoscloud.chat.event.InvitationEvent;
import com.avoscloud.chat.util.Constants;
import com.avoscloud.chat.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.leancloud.chatkit.utils.LCIMNotificationUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/7/10.
 * 自定义推送逻辑
 */
public class LeanchatReceiver extends BroadcastReceiver {

  public final static String AVOS_DATA = "com.avoscloud.Data";

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (!TextUtils.isEmpty(action)) {
      if (action.equals(Constants.INVITATION_ACTION)) {
        String avosData = intent.getStringExtra(AVOS_DATA);
        if (!TextUtils.isEmpty(avosData)) {
          try {
            JSONObject json = new JSONObject(avosData);
            if (null != json) {
              String alertStr = json.getString(PushManager.AVOS_ALERT);

              Intent notificationIntent = new Intent(context, NotificationBroadcastReceiver.class);
              notificationIntent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SYSTEM);
              LCIMNotificationUtils.showNotification(context, "LeanChat", alertStr, notificationIntent);
            }
          } catch (JSONException e) {
            LogUtils.logException(e);
          }
        }
      }
    }
    EventBus.getDefault().post(new InvitationEvent());
  }
}