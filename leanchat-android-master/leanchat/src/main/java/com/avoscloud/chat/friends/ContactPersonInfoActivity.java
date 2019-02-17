package com.avoscloud.chat.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.AVBaseActivity;
import com.avoscloud.chat.activity.ChatRoomActivity;
import com.avoscloud.chat.activity.ZoneActivity;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.Constants;
import com.avoscloud.chat.util.UserCacheUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.leancloud.chatkit.utils.LCIMConstants;

/**
 * 用户详情页，从对话详情页面，查找好友界面和发现页面跳转过来
 */
public class ContactPersonInfoActivity extends AVBaseActivity implements OnClickListener {
  TextView usernameView, genderView;
  ImageView avatarView, avatarArrowView;
  LinearLayout allLayout;//总布局
  Button chatBtn, addFriendBtn;
  RelativeLayout avatarLayout, genderLayout;

  String userId = "";
  LeanchatUser user;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_person_info_activity);
    initData();

    findView();
    initView();
  }

  private void initData() {
    userId = getIntent().getStringExtra(Constants.LEANCHAT_USER_ID);//获取传送过来的用户id
    user = UserCacheUtils.getCachedUser(userId);//获取对应id的user
  }

  private void findView() {
    allLayout = (LinearLayout) findViewById(R.id.all_layout);
    avatarView = (ImageView) findViewById(R.id.avatar_view);//头像
    avatarArrowView = (ImageView) findViewById(R.id.avatar_arrow);
    usernameView = (TextView) findViewById(R.id.username_view);//名字
    avatarLayout = (RelativeLayout) findViewById(R.id.head_layout);//头像布局
    genderLayout = (RelativeLayout) findViewById(R.id.sex_layout);//名字布局

    genderView = (TextView) findViewById(R.id.sexView);//性别
    chatBtn = (Button) findViewById(R.id.chatBtn);//开始会话
    addFriendBtn = (Button) findViewById(R.id.addFriendBtn);//添加好友
  }

  private void initView() {//初始化布局
    LeanchatUser curUser = LeanchatUser.getCurrentUser();//由群组点击进入个人资料
    if (curUser.equals(user)) {
      setTitle(R.string.contact_personalInfo);
      avatarLayout.setOnClickListener(this);//头像布局
      genderLayout.setOnClickListener(this);//性别布局
      avatarArrowView.setVisibility(View.VISIBLE);
      chatBtn.setVisibility(View.GONE);
      addFriendBtn.setVisibility(View.GONE);
    } else {
      setTitle(R.string.contact_detailInfo);
      avatarLayout.setOnClickListener(this);
      avatarArrowView.setVisibility(View.INVISIBLE);
      List<String> cacheFriends = FriendsManager.getFriendIds();
      boolean isFriend = cacheFriends.contains(user.getObjectId());
      if (isFriend) {
        chatBtn.setVisibility(View.VISIBLE);
        chatBtn.setOnClickListener(this);
      } else {
        chatBtn.setVisibility(View.GONE);
        addFriendBtn.setVisibility(View.VISIBLE);
        addFriendBtn.setOnClickListener(this);
      }
    }
    updateView(user);
  }

  private void updateView(LeanchatUser user) {
    Picasso.with(this).load(user.getAvatarUrl()).into(avatarView);
    usernameView.setText(user.getUsername());
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
      case R.id.chatBtn:// 发起聊天
        Intent intent = new Intent(ContactPersonInfoActivity.this, ChatRoomActivity.class);
        intent.putExtra(LCIMConstants.PEER_ID, userId);
        startActivity(intent);
        finish();
        break;
      case R.id.addFriendBtn:// 添加好友
        AddRequestManager.getInstance().createAddRequestInBackground(this, user);
        break;
      case R.id.head_layout:
        Intent intent2=new Intent(ContactPersonInfoActivity.this,ZoneActivity.class);
        intent2.putExtra("lookId", userId);
        startActivity(intent2);
        finish();
        break;
    }
  }
}
