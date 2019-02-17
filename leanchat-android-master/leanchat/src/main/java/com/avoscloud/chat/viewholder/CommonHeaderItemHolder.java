package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.avoscloud.chat.R;

import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

/**
 * Created by wli on 15/11/26.
 */
public class CommonHeaderItemHolder extends LCIMCommonViewHolder {
  LinearLayout rootLayout;

  public CommonHeaderItemHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.common_header_item_layout);
    rootLayout = (LinearLayout)itemView.findViewById(R.id.common_header_root_view);
  }

  public void setView(View view) {
    rootLayout.removeAllViews();
    if (null != view) {
      rootLayout.addView(view);
    }
  }

  @Override
  public void bindData(Object o) {}
}
