package com.avoscloud.chat.friends;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.ChatRoomActivity;
import com.avoscloud.chat.adapter.ContactsAdapter;
import com.avoscloud.chat.event.ContactItemClickEvent;
import com.avoscloud.chat.event.ContactItemLongClickEvent;
import com.avoscloud.chat.event.ContactRefreshEvent;
import com.avoscloud.chat.event.InvitationEvent;
import com.avoscloud.chat.event.MemberLetterEvent;
import com.avoscloud.chat.fragment.BaseFragment;
import com.avoscloud.chat.model.LeanchatUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.utils.LCIMConstants;
import de.greenrobot.event.EventBus;

/**
 * 联系人列表
 * <p/>
 * TODO
 * 1、替换 Fragment 的 title
 * 2、优化 findFriends 代码，现在还是冗余
 */

public class ContactFragment extends BaseFragment {

  @Bind(R.id.activity_square_members_srl_list)
  protected SwipeRefreshLayout refreshLayout;

  @Bind(R.id.activity_square_members_rv_list)
  protected RecyclerView recyclerView;

  private View headerView;//新朋友和群组（固定头）
  ImageView msgTipsView;

  private ContactsAdapter itemAdapter;
  LinearLayoutManager layoutManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    View view = inflater.inflate(R.layout.contact_fragment, container, false);
    headerView = inflater.inflate(R.layout.contact_fragment_header_layout, container, false);
    ButterKnife.bind(this, view);

    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = new ContactsAdapter();
    itemAdapter.setHeaderView(headerView);
    recyclerView.setAdapter(itemAdapter);

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getMembers(false);
      }
    });
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initHeaderView();
    //initHeader();
    refresh();
    EventBus.getDefault().register(this);
    getMembers(false);//加载本地缓存
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onResume() {//返回当前activity时调用
    super.onResume();
    updateNewRequestBadge();
  }

  private void initHeaderView() {
    msgTipsView = (ImageView) headerView.findViewById(R.id.iv_msg_tips);//新朋友通知的红点
    View newView = headerView.findViewById(R.id.layout_new);//新朋友水平整体布局
    newView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ctx, ContactNewFriendActivity.class);//打开新朋友申请通知
        ctx.startActivity(intent);
      }
    });
  }

  private void getMembers(final boolean isforce) {//获取好友list
    FriendsManager.fetchFriends(isforce, new FindCallback<LeanchatUser>() {
      @Override
      public void done(List<LeanchatUser> list, AVException e) {
        refreshLayout.setRefreshing(false);
        itemAdapter.setUserList(list);
        itemAdapter.notifyDataSetChanged();
      }
    });
  }

  private void updateNewRequestBadge() {
    msgTipsView.setVisibility(
      AddRequestManager.getInstance().hasUnreadRequests() ? View.VISIBLE : View.GONE);
  }

  private void refresh() {
    AddRequestManager.getInstance().countUnreadRequests(new CountCallback() {
      @Override
      public void done(int i, AVException e) {
          updateNewRequestBadge();
      }
    });
  }

  public void showDeleteDialog(final String memberId) {
    new AlertDialog.Builder(ctx).setMessage(R.string.contact_deleteContact)
      .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          final ProgressDialog dialog1 = showSpinnerDialog();
          LeanchatUser.getCurrentUser().removeFriend(memberId, new SaveCallback() {
            @Override
            public void done(AVException e) {
              dialog1.dismiss();
              if (filterException(e)) {
                getMembers(true);//加载网络数据
              }
            }
          });
        }
      }).setNegativeButton(R.string.chat_common_cancel, null).show();
  }

  public void onEvent(ContactRefreshEvent event) {//刷新事件
      refresh();//个人修gai
    getMembers(true);////加载网络数据
  }

  public void onEvent(InvitationEvent event) {//邀请事件
    AddRequestManager.getInstance().unreadRequestsIncrement();
    updateNewRequestBadge();
  }

  public void onEvent(ContactItemClickEvent event) {//item点击时间
    Intent intent = new Intent(getActivity(), ChatRoomActivity.class);//进入聊天室界面
    intent.putExtra(LCIMConstants.PEER_ID, event.memberId);
    startActivity(intent);
  }

  public void onEvent(ContactItemLongClickEvent event) {
    showDeleteDialog(event.memberId);
  }//联系人长按事件

  /**
   * 处理 LetterView 发送过来的 MemberLetterEvent
   * 会通过 MembersAdapter 获取应该要跳转到的位置，然后跳转
   */
  public void onEvent(MemberLetterEvent event) {
    Character targetChar = Character.toLowerCase(event.letter);
    if (itemAdapter.getIndexMap().containsKey(targetChar)) {
      int index = itemAdapter.getIndexMap().get(targetChar);
      if (index > 0 && index < itemAdapter.getItemCount()) {
        // 此处 index + 1 是因为 ContactsAdapter 有 header
        layoutManager.scrollToPositionWithOffset(index + 1, 0);
      }
    }
  }
}
