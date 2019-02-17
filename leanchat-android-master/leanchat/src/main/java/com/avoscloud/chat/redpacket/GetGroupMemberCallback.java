package com.avoscloud.chat.redpacket;

import com.yunzhanghu.redpacketsdk.bean.RPUserBean;

import java.util.List;

/**
 * Created by hhx on 16/6/29.
 */
public interface GetGroupMemberCallback {
  void groupInfoSuccess(List<RPUserBean> rpUserList);

  void groupInfoError();
}
