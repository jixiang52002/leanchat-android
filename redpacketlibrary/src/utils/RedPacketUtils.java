package utils;

import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ustc on 2016/5/31.
 */
public class RedPacketUtils {

    public static final String REFRESH_GROUP_RED_PACKET_ACTION = "refresh_group_money_action";
    public static final String EXTRA_RED_PACKET_SENDER_ID = "money_sender_id";
    public static final String EXTRA_RED_PACKET_RECEIVER_ID = "money_receiver_id";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE = "is_open_money_msg";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_MESSAGE = "is_money_msg";
    public static final String EXTRA_RED_PACKET_SENDER_NAME = "money_sender";
    public static final String EXTRA_RED_PACKET_RECEIVER_NAME = "money_receiver";
    public static final String EXTRA_SPONSOR_NAME = "money_sponsor_name";
    public static final String EXTRA_RED_PACKET_GREETING = "money_greeting";
    public static final String EXTRA_RED_PACKET_ID = "ID";
    public static final String CHAT_TYPE = "chatType";
    /**
     * 设置发消息红包的附加字段的attrs
     **/
    public static Map<String, Object>  initSendRedPacketAttrs(boolean isRedpacketMsg,String money_sponsor_name,String money_greeting,String moneyID,int chatType) {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, isRedpacketMsg);
        attrs.put(EXTRA_SPONSOR_NAME, money_sponsor_name);
        attrs.put(EXTRA_RED_PACKET_GREETING, money_greeting);
        attrs.put(EXTRA_RED_PACKET_ID, moneyID);
        attrs.put(CHAT_TYPE, chatType);

        return  attrs;
    }

    public static RedPacketInfo initRedPacketInfo_single(String fromNickname,String fromAvatarUrl, String toUserId,int chatType){
        RedPacketInfo redPacketInfo=new RedPacketInfo();
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.toUserId =toUserId;
        redPacketInfo.chatType = chatType;
        return redPacketInfo;
    }

    public static RedPacketInfo initRedPacketInfo_group(String fromNickname,String fromAvatarUrl, String toUserId,int chatType,String toGroupId,int groupMemberCount){
        RedPacketInfo redPacketInfo=new RedPacketInfo();
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.toUserId =toUserId;
        redPacketInfo.chatType = chatType;
        redPacketInfo.toGroupId = toGroupId;
        redPacketInfo.groupMemberCount = groupMemberCount;
        redPacketInfo.chatType = chatType;
        return redPacketInfo;
    }



}
