package com.avoscloud.chat.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.avoscloud.chat.R;
//会话标题栏设计
/**
 * Created by lzw on 14-9-17.
 */
public class HeaderLayout extends LinearLayout {
  LayoutInflater mInflater;
  RelativeLayout header;
  TextView titleView;
  LinearLayout leftContainer, rightContainer;
  Button backBtn;

  public HeaderLayout(Context context) {
    super(context);
    init();
  }

  public HeaderLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    mInflater = LayoutInflater.from(getContext());//加载布局管理器
    header = (RelativeLayout) mInflater.inflate(R.layout.chat_common_base_header, null, false);//将xml布局转换为view对象
    titleView = (TextView) header.findViewById(R.id.titleView);//利用xml转化的view对象，找到布局中的组件
    leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
    rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
    backBtn = (Button) header.findViewById(R.id.backBtn);
    addView(header);//配合inflate第三个参数为false使用
  }

  public void showTitle(int titleId) {
    titleView.setText(titleId);
  }

  public void showTitle(String s) {
    titleView.setText(s);
  }

  public void showLeftBackButton(OnClickListener listener) {
    showLeftBackButton(R.string.chat_common_emptyStr, listener);
  }

  public void showLeftBackButton() {
    showLeftBackButton(null);
  }

  public void showLeftBackButton(int backTextId, OnClickListener listener) {
    backBtn.setVisibility(View.VISIBLE);
    backBtn.setText(backTextId);
    if (listener == null) {
      listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
          ((Activity) getContext()).finish();
        }
      };
    }
    backBtn.setOnClickListener(listener);
  }

  public void showRightImageButton(int rightResId, OnClickListener listener) {
    View imageViewLayout = mInflater.inflate(R.layout.chat_common_base_header_right_image_btn, null, false);
    ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
    rightButton.setImageResource(rightResId);
    rightButton.setOnClickListener(listener);
    rightContainer.addView(imageViewLayout);
  }
}
