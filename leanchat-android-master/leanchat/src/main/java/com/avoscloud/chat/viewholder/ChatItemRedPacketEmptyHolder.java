package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.avoscloud.chat.R;

import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

/**
 * Created by ust on 2016/6/2.
 */
public class ChatItemRedPacketEmptyHolder extends LCIMCommonViewHolder {

  View view;

  public ChatItemRedPacketEmptyHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.lc_chat_item_empty);
  }

  @Override
  public void bindData(Object o) {
  }
}
