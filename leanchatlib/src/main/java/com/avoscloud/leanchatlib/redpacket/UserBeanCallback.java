package com.avoscloud.leanchatlib.redpacket;


import com.yunzhanghu.redpacketsdk.bean.RPUserBean;

/**
 * Created by hhx on 16/6/30.
 */
public interface UserBeanCallback {
  void getUserInfo(RPUserBean userbean);
}
