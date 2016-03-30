package com.avoscloud.chat.util;

import com.avoscloud.chat.model.LeanchatUser;

import java.util.ArrayList;
import java.util.List;

import cn.leanclud.imkit.LCIMProfileProvider;
import cn.leanclud.imkit.LCIMProfilesCallBack;
import cn.leanclud.imkit.LCIMUserProfile;

/**
 * Created by wli on 15/12/4.
 */
public class LeanchatUserProvider implements LCIMProfileProvider {

  private static LCIMUserProfile getThirdPartUser(LeanchatUser leanchatUser) {
    return new LCIMUserProfile(leanchatUser.getObjectId(), leanchatUser.getUsername(), leanchatUser.getAvatarUrl());
  }

  private static List<LCIMUserProfile> getThirdPartUsers(List<LeanchatUser> leanchatUsers) {
    List<LCIMUserProfile> thirdPartUsers = new ArrayList<>();
    for (LeanchatUser user : leanchatUsers) {
      thirdPartUsers.add(getThirdPartUser(user));
    }
    return thirdPartUsers;
  }

  @Override
  public void getProfiles(List<String> list, final LCIMProfilesCallBack callBack) {
    UserCacheUtils.fetchUsers(list, new UserCacheUtils.CacheUserCallback() {
      @Override
      public void done(List<LeanchatUser> userList, Exception e) {
        callBack.done(getThirdPartUsers(userList), e);
      }
    });
  }
}
