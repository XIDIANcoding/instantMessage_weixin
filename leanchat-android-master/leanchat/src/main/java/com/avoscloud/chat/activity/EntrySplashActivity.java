package com.avoscloud.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LeanchatUser;

import cn.leancloud.chatkit.LCChatKit;

public class EntrySplashActivity extends AVBaseActivity {
  public static final int SPLASH_DURATION = 2000;
  private static final int GO_MAIN_MSG = 1;
  private static final int GO_LOGIN_MSG = 2;

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case GO_MAIN_MSG:
          imLogin();
          break;
        case GO_LOGIN_MSG:
          Intent intent = new Intent(EntrySplashActivity.this, EntryLoginActivity.class);
          EntrySplashActivity.this.startActivity(intent);
          finish();
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.entry_splash_layout);
    if (LeanchatUser.getCurrentUser() != null) {
      LeanchatUser.getCurrentUser().updateUserInfo();
      handler.sendEmptyMessageDelayed(GO_MAIN_MSG, SPLASH_DURATION);//延迟两秒将消息加入队列
    } else {
      handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
    }
  }

  private void imLogin() {
    LCChatKit.getInstance().open(LeanchatUser.getCurrentUserId(), new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (filterException(e)) {
          AVObject todo = AVObject.createWithoutData("_User", LeanchatUser.getCurrentUserId());
          todo.put("state","（在线）");
          todo.saveInBackground();
          Intent intent = new Intent(EntrySplashActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
        }
      }
    });
  }
}
