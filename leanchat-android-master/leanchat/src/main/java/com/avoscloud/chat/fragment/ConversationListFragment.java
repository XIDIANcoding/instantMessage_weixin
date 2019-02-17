package com.avoscloud.chat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avoscloud.chat.R;
import com.avoscloud.chat.view.HeaderLayout;

import cn.leancloud.chatkit.activity.LCIMConversationListFragment;

/**
 * Created by wli on 16/3/29.
 */
public class ConversationListFragment extends LCIMConversationListFragment {

  protected HeaderLayout headerLayout;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);//使用LCIMCoonsersationListFraagment的界面
    headerLayout = (HeaderLayout) view.findViewById(R.id.headerLayout);
    headerLayout.showTitle(R.string.conversation_messages);
    return view;
  }
}
