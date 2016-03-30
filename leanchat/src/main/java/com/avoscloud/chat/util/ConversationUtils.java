package com.avoscloud.chat.util;

import android.text.TextUtils;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avoscloud.chat.model.ConversationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leanclud.imkit.LCIMKit;
import cn.leanclud.imkit.LCIMUserProfile;
import cn.leanclud.imkit.cache.LCIMProfileCache;
import cn.leanclud.imkit.utils.LCIMConversationUtils;

/**
 * Created by wli on 16/3/30.
 */
public class ConversationUtils extends LCIMConversationUtils {

  public static ConversationType typeOfConversation(AVIMConversation conversation) {
    if (isValidConversation(conversation)) {
      Object typeObject = conversation.getAttribute(ConversationType.TYPE_KEY);
      int typeInt = (Integer) typeObject;
      return ConversationType.fromInt(typeInt);
    } else {
//      LogUtils.e("invalid conversation ");
      // 因为 Group 不需要取 otherId，检查没那么严格，避免导致崩溃
      return ConversationType.Group;
    }
  }

  public static void createGroupConversation(final List<String> memberIds, final AVIMConversationCreatedCallback callback) {
    LCIMProfileCache.getInstance().getCachedUsers(memberIds, new AVCallback<List<LCIMUserProfile>>() {
      @Override
      protected void internalDone0(List<LCIMUserProfile> lcimUserProfiles, AVException e) {
        List<String> nameList = new ArrayList<String>();
        for (LCIMUserProfile userProfile : lcimUserProfiles) {
          nameList.add(userProfile.getUserName());
        }

        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ConversationType.TYPE_KEY, ConversationType.Group.getValue());
        attrs.put("name", TextUtils.join(",", nameList));
        LCIMKit.getInstance().getClient().createConversation(memberIds, "", attrs, false, true, callback);
      }
    });
  }

  public static void createSingleConversation(String memberId, AVIMConversationCreatedCallback callback) {
    Map<String, Object> attrs = new HashMap<>();
    attrs.put(ConversationType.TYPE_KEY, ConversationType.Single.getValue());
    LCIMKit.getInstance().getClient().createConversation(Arrays.asList(memberId), "", attrs, false, true, callback);
  }

  public static void findGroupConversationsIncludeMe(AVIMConversationQueryCallback callback) {
    AVIMConversationQuery conversationQuery = LCIMKit.getInstance().getClient().getQuery();
    if (null != conversationQuery) {
      conversationQuery.containsMembers(Arrays.asList(LCIMKit.getInstance().getCurrentUserId()));
      conversationQuery.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Group.getValue());
      conversationQuery.orderByDescending(Constants.UPDATED_AT);
      conversationQuery.limit(1000);
      conversationQuery.findInBackground(callback);
    } else if (null != callback) {
      callback.done(new ArrayList<AVIMConversation>(), null);
    }
  }

  public static boolean isValidConversation(AVIMConversation conversation) {
    if (conversation == null) {
//      LogUtils.d("invalid reason : conversation is null");
      return false;
    }
    if (conversation.getMembers() == null || conversation.getMembers().size() == 0) {
//      LogUtils.d("invalid reason : conversation members null or empty");
      return false;
    }
    Object type = conversation.getAttribute(ConversationType.TYPE_KEY);
    if (type == null) {
//      LogUtils.d("invalid reason : type is null");
      return false;
    }

    int typeInt = (Integer) type;
    if (typeInt == ConversationType.Single.getValue()) {
      if (conversation.getMembers().size() != 2 ||
        conversation.getMembers().contains(LCIMKit.getInstance().getCurrentUserId()) == false) {
//        LogUtils.d("invalid reason : oneToOne conversation not correct");
        return false;
      }
    } else if (typeInt == ConversationType.Group.getValue()) {

    } else {
//      LogUtils.d("invalid reason : typeInt wrong");
      return false;
    }
    return true;
  }
}
