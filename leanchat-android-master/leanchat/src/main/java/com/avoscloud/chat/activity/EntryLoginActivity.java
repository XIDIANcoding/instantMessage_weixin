package com.avoscloud.chat.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.leancloud.chatkit.LCChatKit;

public class EntryLoginActivity extends AVBaseActivity {

  @Bind(R.id.activity_login_et_username)//butterknife使用
  public EditText userNameView;

  @Bind(R.id.activity_login_et_password)
  public EditText passwordView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.entry_login_activity);
  }

  @OnClick(R.id.activity_login_btn_login)
  public void onLoginClick(View v) {
    login();
  }

  @OnClick(R.id.activity_login_btn_register)
  public void onRegisterClick(View v) {
    Intent intent = new Intent(this, EntryRegisterActivity.class);
    startActivity(intent);
  }
  @OnClick(R.id.activity_searchpass_btn_register)
  public void onSearchPass(View v){
    new AlertDialog.Builder(EntryLoginActivity.this).setMessage("重置密码")
            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog dialog1 = showSpinnerDialog();
                dialog1.dismiss();
                Intent intent = new Intent(EntryLoginActivity.this,FindBackPasswordActivity.class);
                startActivity(intent);
              }
            })
            .setNegativeButton("取消",null).show();
      }
  private void login() {
    final String name = userNameView.getText().toString().trim();
    final String password = passwordView.getText().toString().trim();

    if (TextUtils.isEmpty(name)) {
      Utils.toast(R.string.username_cannot_null);
      return;
    }

    if (TextUtils.isEmpty(password)) {
      Utils.toast(R.string.password_can_not_null);
      return;
    }

    final ProgressDialog dialog = showSpinnerDialog();
    LeanchatUser.logInInBackground(name, password, new LogInCallback<LeanchatUser>() {
      @Override
      public void done(LeanchatUser avUser, AVException e) {
        dialog.dismiss();
        if (filterException(e)) {
          imLogin();
        }
      }
    }, LeanchatUser.class);
  }

  /**
   * 因为 leancloud 实时通讯与账户体系是完全解耦的，所以此处需要先 LeanchatUser.logInInBackground
   * 如果验证账号密码成功，然后再 openClient 进行实时通讯
   */
  public void imLogin() {

    LCChatKit.getInstance().open(LeanchatUser.getCurrentUserId(), new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (filterException(e)) {
          AVObject todo = AVObject.createWithoutData("_User", LeanchatUser.getCurrentUserId());
          todo.put("state","（在线）");
          todo.saveInBackground();
          Intent intent = new Intent(EntryLoginActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
        }
      }
    });
  }
}
