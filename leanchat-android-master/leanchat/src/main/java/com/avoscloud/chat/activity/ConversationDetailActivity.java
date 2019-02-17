package com.avoscloud.chat.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.HeaderListAdapter;
import com.avoscloud.chat.event.ConversationMemberClickEvent;
import com.avoscloud.chat.friends.ContactPersonInfoActivity;
import com.avoscloud.chat.model.ConversationType;
import com.avoscloud.chat.util.Constants;
import com.avoscloud.chat.util.ConversationUtils;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.chat.viewholder.ConversationDetailItemHolder;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.UserCacheUtils;
import com.avoscloud.chat.util.UserCacheUtils.CacheUserCallback;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.utils.LCIMConstants;


public class ConversationDetailActivity extends AVBaseActivity {
  private static final int ADD_MEMBERS = 0;
  private static final int INTENT_NAME = 1;

  @Bind(R.id.activity_conv_detail_rv_list)
  RecyclerView recyclerView;

  GridLayoutManager layoutManager;
  HeaderListAdapter<LeanchatUser> listAdapter;

  View nameLayout;//群聊名称
  View quitLayout;//删除并推出群聊

  ConversationType conversationType;

  private AVIMConversation conversation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.conversation_detail_activity);
    String conversationId = getIntent().getStringExtra(LCIMConstants.CONVERSATION_ID);
    conversation = LCChatKit.getInstance().getClient().getConversation(conversationId);

    View footerView = getLayoutInflater().inflate(R.layout.conversation_detail_footer_layout, null);
    nameLayout = footerView.findViewById(R.id.name_layout);
    nameLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        gotoModifyNameActivity();
      }
    });
    quitLayout = footerView.findViewById(R.id.quit_layout);
    quitLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        quitGroup();
      }
    });

    layoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        return (listAdapter.getItemViewType(position) == HeaderListAdapter.FOOTER_ITEM_TYPE ? layoutManager.getSpanCount() : 1);
      }
    });
    listAdapter = new HeaderListAdapter<>(ConversationDetailItemHolder.class);
    listAdapter.setFooterView(footerView);

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(listAdapter);
    setTitle(R.string.conversation_detail_title);
    conversationType = ConversationUtils.typeOfConversation(conversation);
    setViewByConvType(conversationType);
  }

  private void setViewByConvType(ConversationType conversationType) {
    if (conversationType == ConversationType.Single) {//两人为单聊，两人以上为群聊
      nameLayout.setVisibility(View.GONE);
      quitLayout.setVisibility(View.GONE);
    } else {
      nameLayout.setVisibility(View.VISIBLE);
      quitLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    refresh();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem invite = menu.add(0, ADD_MEMBERS, 0, R.string.conversation_detail_invite);
    alwaysShowMenuItem(menu);
    return super.onCreateOptionsMenu(menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    int menuId = item.getItemId();
    if (menuId == ADD_MEMBERS) {
      Intent intent = new Intent(this, ConversationAddMembersActivity.class);
      intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
      startActivityForResult(intent, ADD_MEMBERS);
    }
    return super.onOptionsItemSelected(item);
  }

  private void refresh() {
    UserCacheUtils.fetchUsers(conversation.getMembers(), new CacheUserCallback() {
      @Override
      public void done(List<LeanchatUser> userList, Exception e) {
        listAdapter.setDataList(userList);
        listAdapter.notifyDataSetChanged();
      }
    });
  }

  public void onEvent(ConversationMemberClickEvent clickEvent) {
    if (clickEvent.isLongClick) {
      removeMemeber(clickEvent.memberId);//删除群成员
    } else {
      gotoPersonalActivity(clickEvent.memberId);//进入群成员资料界面
    }
  }

  private void gotoPersonalActivity(String memberId) {
    Intent intent = new Intent(this, ContactPersonInfoActivity.class);
    intent.putExtra(Constants.LEANCHAT_USER_ID, memberId);
    startActivity(intent);
  }

  private void gotoModifyNameActivity() {
    Intent intent = new Intent(this, UpdateContentActivity.class);
    intent.putExtra(Constants.INTENT_KEY, getString(R.string.conversation_name));
    startActivityForResult(intent, INTENT_NAME);
  }

  private void removeMemeber(final String memberId) {
    if (conversationType == ConversationType.Single) {
      return;
    }
    boolean isTheOwner = conversation.getCreator().equals(memberId);
    if (!isTheOwner) {
      new AlertDialog.Builder(this).setMessage(R.string.conversation_kickTips)
        .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            final ProgressDialog progress = showSpinnerDialog();
            conversation.kickMembers(Arrays.asList(memberId), new AVIMConversationCallback() {
              @Override
              public void done(AVIMException e) {
                progress.dismiss();
                if (filterException(e)) {
                  Utils.toast(R.string.conversation_detail_kickSucceed);
                  refresh();
                }
              }
            });
          }
        }).setNegativeButton(R.string.chat_common_cancel, null).show();
    }
  }

  /**
   * 退出群聊
   */
  private void quitGroup() {
    new AlertDialog.Builder(this).setMessage(R.string.conversation_quit_group_tip)
      .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          final String convid = conversation.getConversationId();
          conversation.quit(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
              if (filterException(e)) {
                LCIMConversationItemCache.getInstance().deleteConversation(convid);
                Utils.toast(R.string.conversation_alreadyQuitConv);
                setResult(RESULT_OK);
                finish();
              }
            }
          });
        }
      }).setNegativeButton(R.string.chat_common_cancel, null).show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == INTENT_NAME) {
        String newName = data.getStringExtra(Constants.INTENT_VALUE);
        updateName(conversation, newName, new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            if (filterException(e)) {
              refresh();
            }
          }
        });
      } else if (requestCode == ADD_MEMBERS) {
        refresh();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void updateName(final AVIMConversation conv, String newName, final AVIMConversationCallback callback) {
    conv.setName(newName);
    conv.updateInfoInBackground(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (e != null) {
          if (callback != null) {
            callback.done(e);
          }
        } else {
          if (callback != null) {
            callback.done(null);
          }
        }
      }
    });
  }
}
