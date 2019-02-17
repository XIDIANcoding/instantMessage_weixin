package com.avoscloud.chat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.MemeberAddAdapter;
import com.avoscloud.chat.friends.FriendsManager;
import com.avoscloud.chat.model.ConversationType;
import com.avoscloud.chat.util.ConversationUtils;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.UserCacheUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.utils.LCIMConstants;

/
public class ConversationAddMembersActivity extends AVBaseActivity {

  @Bind(R.id.member_add_rv_list)
  protected RecyclerView recyclerView;

  private LinearLayoutManager layoutManager;
  private MemeberAddAdapter adapter;
  private AVIMConversation conversation;

  public static final int OK = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.conversation_add_members_layout);
    String conversationId = getIntent().getStringExtra(LCIMConstants.CONVERSATION_ID);
    conversation = LCChatKit.getInstance().getClient().getConversation(conversationId);

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new MemeberAddAdapter();
    recyclerView.setAdapter(adapter);

    setListData();
  }

  private void setListData() {
    FriendsManager.fetchFriends(false, new FindCallback<LeanchatUser>() {
      @Override
      public void done(List<LeanchatUser> list, AVException e) {
        if (filterException(e)) {
          final List<String> userIds = new ArrayList<String>();
          for (LeanchatUser user : list) {
            userIds.add(user.getObjectId());
          }
          userIds.removeAll(conversation.getMembers());
          UserCacheUtils.fetchUsers(userIds, new UserCacheUtils.CacheUserCallback() {
            @Override
            public void done(List<LeanchatUser> userList, Exception e) {
              adapter.setDataList(userList);
              adapter.notifyDataSetChanged();
            }
          });
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem add = menu.add(0, OK, 0, R.string.common_sure);
    alwaysShowMenuItem(menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == OK) {
      addMembers();
    }
    return super.onOptionsItemSelected(item);
  }

  private void addMembers() {
    final List<String> checkedUsers = adapter.getCheckedIds();
    final ProgressDialog dialog = showSpinnerDialog();
    if (checkedUsers.size() == 0) {
      finish();
    } else {
      if (ConversationUtils.typeOfConversation(conversation) == ConversationType.Single) {
        List<String> members = new ArrayList<String>();
        members.addAll(checkedUsers);
        members.addAll(conversation.getMembers());
        ConversationUtils.createGroupConversation(members, new AVIMConversationCreatedCallback() {
          @Override
          public void done(final AVIMConversation conversation, AVIMException e) {
            if (filterException(e)) {
              Intent intent = new Intent(ConversationAddMembersActivity.this, ChatRoomActivity.class);
              intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
              startActivity(intent);
              finish();
            }
          }
        });
      } else {
        conversation.addMembers(checkedUsers, new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            dialog.dismiss();
            if (filterException(e)) {
              Utils.toast(R.string.conversation_inviteSucceed);
              setResult(RESULT_OK);
              finish();
            }
          }
        });
      }
    }
  }
}
