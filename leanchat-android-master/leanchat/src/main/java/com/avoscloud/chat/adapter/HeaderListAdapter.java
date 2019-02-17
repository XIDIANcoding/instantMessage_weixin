package com.avoscloud.chat.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.avoscloud.chat.viewholder.CommonFooterItemHolder;
import com.avoscloud.chat.viewholder.CommonHeaderItemHolder;

import cn.leancloud.chatkit.adapter.LCIMCommonListAdapter;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;


/**
 * Created by wli on 15/11/25.
 * 现在还仅仅支持单类型 item，多类型 item 稍后在重构
 */
public class HeaderListAdapter<T> extends LCIMCommonListAdapter<T> {

  public static final int HEADER_ITEM_TYPE = -1;
  public static final int FOOTER_ITEM_TYPE = -2;
  public static final int COMMON_ITEM_TYPE = 1;

  private View headerView = null;
  private View footerView = null;

  public HeaderListAdapter(Class<?> vhClass) {
    super(vhClass);
  }

  public void setHeaderView(View view) {
    headerView = view;
  }

  public void setFooterView(View view) {
    footerView = view;
  }

  @Override
  public int getItemCount() {
    int itemCount = super.getItemCount();
    if (null != headerView) {
      ++itemCount;
    }
    if (null != footerView) {
      ++itemCount;
    }
    return itemCount;
  }

  @Override
  public long getItemId(int position) {
    if (null != headerView && 0 == position) {
      return -1;
    }

    if (null != footerView && position == getItemCount() - 1) {
      return -2;
    }
    return super.getItemId(position - 1);
  }

  @Override
  public int getItemViewType(int position) {
    if (null != headerView && 0 == position) {
      return HEADER_ITEM_TYPE;
    }

    if (null != footerView && position == getItemCount() - 1) {
      return FOOTER_ITEM_TYPE;
    }

    return COMMON_ITEM_TYPE;
  }

  @Override
  public void onBindViewHolder(LCIMCommonViewHolder holder, int position) {
    int truePosition = position;
    if (null != headerView) {
      if (0 == position) {
        return;
      } else {
        truePosition = position - 1;
      }
    }

    if (null != footerView && position == getItemCount() - 1) {
      return;
    }
    super.onBindViewHolder(holder, truePosition);
  }

  @Override
  public LCIMCommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (HEADER_ITEM_TYPE == viewType) {
      CommonHeaderItemHolder itemHolder = new CommonHeaderItemHolder(parent.getContext(), parent);
      itemHolder.setView(headerView);
      return itemHolder;
    }

    if (FOOTER_ITEM_TYPE == viewType) {
      CommonFooterItemHolder itemHolder = new CommonFooterItemHolder(parent.getContext(), parent);
      itemHolder.setView(footerView);
      return itemHolder;
    }

    return super.onCreateViewHolder(parent, viewType);
  }
}
