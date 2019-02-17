package com.avoscloud.chat.activity;

import android.os.Bundle;
import com.avoscloud.chat.R;


public class ProfileNotifySettingActivity extends AVBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.profile_setting_notify_layout);
    setTitle(R.string.profile_notifySetting);
  }
}
