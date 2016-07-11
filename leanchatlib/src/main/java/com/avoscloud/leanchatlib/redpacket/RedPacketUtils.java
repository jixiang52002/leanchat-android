package com.avoscloud.leanchatlib.redpacket;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yunzhanghu.redpacketsdk.bean.RPUserBean;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.callback.GroupMemberCallback;
import com.yunzhanghu.redpacketui.callback.NotifyGroupMemberCallback;
import com.yunzhanghu.redpacketui.ui.activity.RPChangeActivity;
import com.yunzhanghu.redpacketui.ui.activity.RPRedPacketActivity;
import com.yunzhanghu.redpacketui.utils.RPGroupMemberUtil;

import java.util.List;

/**
 * Created by ustc on 2016/5/31.
 */
public class RedPacketUtils {

    public static final String EXTRA_RED_PACKET_SENDER_ID = "money_sender_id";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE = "is_open_money_msg";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_MESSAGE = "is_money_msg";
    public static final String EXTRA_RED_PACKET_SENDER_NAME = "money_sender";
    public static final String EXTRA_RED_PACKET_RECEIVER_NAME = "money_receiver";
    public static final String EXTRA_RED_PACKET_RECEIVER_ID = "money_receiver_id";
    public static final String EXTRA_SPONSOR_NAME = "money_sponsor_name";
    public static final String EXTRA_RED_PACKET_GREETING = "money_greeting";
    public static final String EXTRA_RED_PACKET_ID = "ID";
    public static final String MESSAGE_DIRECT_SEND = "SEND";
    public static final String MESSAGE_DIRECT_RECEIVE = "RECEIVE";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_RED_PACKET = "redpacket";
    public static final String KEY_RED_PACKET_USER = "redpacket_user";
    public static final String KEY_TYPE = "type";
    public static final String VALUE_TYPE = "redpacket_taken";
    public static final String KEY_RED_PACKET_TYPE = "red_packet_type";
    public static final String KEY_RED_PACKET_SPECIAL_RECEIVEID = "special_money_receiver_id";
    private static RedPacketUtils mRedPacketUtil;

    private GetUserInfoCallback mGetUserInfoCallback;
    private GetUserBeanCallback mGetUserBeanCallback;
    private TokenData mTokenData;//打开红包时需要转登录时获取的数据
    private String userid;//当前用户id;
    private String userName;//当前用户姓名
    private String userAvatar;//当前用户头像

    private RedPacketUtils() {

    }

    public static RedPacketUtils getInstance() {
        if (mRedPacketUtil == null) {
            synchronized (RedPacketUtils.class) {
                if (mRedPacketUtil == null) {
                    mRedPacketUtil = new RedPacketUtils();
                }

            }
        }
        return mRedPacketUtil;
    }


    public static RedPacketInfo initRedPacketInfo_single(String fromNickname, String fromAvatarUrl, String toUserId, int chatType) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.toUserId = toUserId;
        redPacketInfo.chatType = chatType;
        return redPacketInfo;
    }

    public static RedPacketInfo initRedPacketInfo_group(String fromNickname, String fromAvatarUrl, String toUserId, int chatType, String toGroupId, int groupMemberCount) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;//发送人的头像
        redPacketInfo.fromNickName = fromNickname;//发送人的名字
        redPacketInfo.toUserId = toUserId;
        redPacketInfo.chatType = chatType;//判断是否是单聊
        redPacketInfo.toGroupId = toGroupId;//群id
        redPacketInfo.groupMemberCount = groupMemberCount;//群成员数量
//        redPacketInfo.chatType = chatType;
        return redPacketInfo;
    }

    /**
     * 打开普通红包用
     *
     * @param fromNickname
     * @param fromAvatarUrl
     * @param moneyMsgDirect
     * @param chatType
     * @param moneyId
     * @return
     */
    public static RedPacketInfo initRedPacketInfo_received(String fromNickname, String fromAvatarUrl, String moneyMsgDirect, int chatType, String moneyId) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.moneyMsgDirect = moneyMsgDirect;
        redPacketInfo.chatType = chatType;
        redPacketInfo.moneyID = moneyId;
        redPacketInfo.toAvatarUrl = fromAvatarUrl;
        redPacketInfo.toNickName = fromNickname;
        return redPacketInfo;
    }

    /**
     * 打开专属红包用
     *
     * @param rpUserBean
     * @param fromNickname
     * @param fromAvatarUrl
     * @param moneyMsgDirect
     * @param chatType
     * @param moneyId
     * @return
     */
    public static RedPacketInfo initRedPacketInfo_received(RPUserBean rpUserBean, String fromNickname, String fromAvatarUrl, String moneyMsgDirect, int chatType, String moneyId) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.moneyMsgDirect = moneyMsgDirect;
        redPacketInfo.chatType = chatType;
        redPacketInfo.moneyID = moneyId;
        redPacketInfo.toAvatarUrl = fromAvatarUrl;
        redPacketInfo.toNickName = fromNickname;
        redPacketInfo.specialNickname = rpUserBean.userNickname;
        redPacketInfo.specialAvatarUrl = rpUserBean.userAvatar;
        redPacketInfo.toUserId = RedPacketUtils.getInstance().getUserid();
        return redPacketInfo;
    }

    /**
     * 发红包时用
     *
     * @param fragment
     * @param toUserId
     * @param fromNickname
     * @param fromAvatarUrl
     * @param chatType
     * @param tpGroupId
     * @param membersNum
     * @param REQUEST_CODE_SEND_MONEY
     */
    public static void selectRedPacket(Fragment fragment, String toUserId, String fromNickname, String fromAvatarUrl, int chatType, String tpGroupId, int membersNum, int REQUEST_CODE_SEND_MONEY) {
        Intent intent = new Intent(fragment.getActivity(), RPRedPacketActivity.class); /*接收者Id或者接收的群Id*/
        RedPacketInfo redpacketInfo;
        if (chatType == RPConstant.CHATTYPE_SINGLE) {
            redpacketInfo = initRedPacketInfo_single(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_SINGLE);
        } else if (chatType == RPConstant.CHATTYPE_GROUP) {
            redpacketInfo = initRedPacketInfo_group(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_GROUP, tpGroupId, membersNum);
        } else {
            return;
        }
        intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redpacketInfo);
        intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, RedPacketUtils.getInstance().getmTokenData());
        fragment.startActivityForResult(intent, REQUEST_CODE_SEND_MONEY);
    }

    /**
     * 群红包中发专属红包用的
     *
     * @param rpUserlist
     */
    public void initRpGroupMember(final List<RPUserBean> rpUserlist) {
        RPGroupMemberUtil.getInstance().setGroupMemberListener(new NotifyGroupMemberCallback() {
            @Override
            public void getGroupMember(String s, GroupMemberCallback groupMemberCallback) {
                groupMemberCallback.setGroupMember(rpUserlist);
            }
        });
    }

    /**
     * 发红包、拆红包、进入零钱页都需要
     *
     * @param authPartner
     * @param authUserId
     * @param authTimestamp
     * @param authSign
     */
    public void initTokenData(String authPartner, String authUserId, String authTimestamp, String authSign) {
        mTokenData = new TokenData();
        mTokenData.authPartner = authPartner;
        mTokenData.appUserId = authUserId;
        mTokenData.authTimestamp = authTimestamp;
        mTokenData.authSign = authSign;
    }

    public TokenData getmTokenData() {
        return mTokenData;
    }

    public void setmTokenData(TokenData mAuthData) {
        this.mTokenData = mAuthData;
    }

    /**
     * 进入零钱页用
     *
     * @param mContext
     */
    public void toChangeActivity(Context mContext) {
        Intent intent = new Intent(mContext, RPChangeActivity.class);
        // intent.putExtra(RPConstant.EXTRA_USER_NAME, userName);
        // intent.putExtra(RPConstant.EXTRA_TO_USER_AVATAR, userAvatar);
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromNickName = userName;
        redPacketInfo.fromAvatarUrl = userAvatar;
        intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
        intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, getmTokenData());
        mContext.startActivity(intent);
    }

    public GetUserInfoCallback getmGetUserInfoCallback() {
        return mGetUserInfoCallback;
    }

    public void setmGetUserInfoCallback(GetUserInfoCallback mGetUserInfoCallback) {
        this.mGetUserInfoCallback = mGetUserInfoCallback;
    }

    public GetUserBeanCallback getmGetUserBeanCallback() {
        return mGetUserBeanCallback;
    }

    public void setmGetUserBeanCallback(GetUserBeanCallback mGetUserBeanCallback) {
        this.mGetUserBeanCallback = mGetUserBeanCallback;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}
