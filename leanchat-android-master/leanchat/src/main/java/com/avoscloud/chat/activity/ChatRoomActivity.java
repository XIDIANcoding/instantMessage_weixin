package com.avoscloud.chat.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.util.ConversationUtils;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;


public class ChatRoomActivity extends LCIMConversationActivity {

  private AVIMConversation conversation;

  public static final int QUIT_GROUP_REQUEST = 200;

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();// // 获取当前的菜单
    inflater.inflate(R.menu.chat_ativity_menu, menu);//// 填充菜单
    if (null != menu && menu.size() > 0) {
      MenuItem item = menu.getItem(0);
      item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS//无论标题是否溢出，item总会显示
        | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void updateConversation(AVIMConversation conversation) {
    super.updateConversation(conversation);
    this.conversation = conversation;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {// 对菜单点击事件处理
    int menuId = item.getItemId();
    if (menuId == R.id.people) {
      if (null != conversation) {//点击右上角小人进入邀请
        Intent intent = new Intent(ChatRoomActivity.this, ConversationDetailActivity.class);
        intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
        startActivityForResult(intent, QUIT_GROUP_REQUEST);//可以一次性完成这项任务，当程序执行到这段代码的时候，假若从T1Activity跳转到下一个Text2Activity，
        // 而当这个Text2Activity调用了finish()方法以后，程序会自动跳转回T1Activity，并调用前一个T1Activity中的onActivityResult( )方法。
        //此处处理在ConversationDetailActivity时按返回键返回当前activity
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case QUIT_GROUP_REQUEST:
          finish();
          break;
      }
    }
  }

  @Override
  protected void getConversation(String memberId) {
    super.getConversation(memberId);
    ConversationUtils.createSingleConversation(memberId, new AVIMConversationCreatedCallback() {
      @Override
      public void done(AVIMConversation avimConversation, AVIMException e) {
        updateConversation(avimConversation);
      }
    });
  }
}