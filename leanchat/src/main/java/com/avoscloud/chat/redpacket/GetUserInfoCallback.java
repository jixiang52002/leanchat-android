package com.avoscloud.chat.redpacket;

import java.util.List;

/**
 * Created by hhx on 16/6/29.
 */
public interface GetUserInfoCallback {
  void done(List<String> ids, UserInfoCallback callback);
}
