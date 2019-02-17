package com.avoscloud.chat.redpacket;

import com.yunzhanghu.redpacketsdk.bean.TokenData;

/**
 * Created by hhx on 16/7/27.
 */
public interface GetSignInfoCallback {
  void signInfoSuccess(TokenData tokenData);

  void signInfoError(String errorMsg);
}
