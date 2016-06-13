package utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketui.R;
import com.easemob.redpacketui.ui.activity.RPRedPacketActivity;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ustc on 2016/5/31.
 */
public class RedPacketUtils {

    public static final String EXTRA_RED_PACKET_SENDER_ID = "money_sender_id";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE = "is_open_money_msg";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_MESSAGE = "is_money_msg";
    public static final String EXTRA_RED_PACKET_SENDER_NAME = "money_sender";
    public static final String EXTRA_RED_PACKET_RECEIVER_NAME = "money_receiver";
    public static final String EXTRA_SPONSOR_NAME = "money_sponsor_name";
    public static final String EXTRA_RED_PACKET_GREETING = "money_greeting";
    public static final String EXTRA_RED_PACKET_ID = "ID";
    public static final String CHAT_TYPE = "chatType";
    public static final String MESSAGE_DIRECT_SEND = "SEND";
    public static final String MESSAGE_DIRECT_RECEIVE = "RECEIVE";

//    /**
//     * 设置发消息红包的附加字段的attrs
//     **/
//    public static Map<String, Object> initSendRedPacketAttrs(boolean isRedpacketMsg, String money_sponsor_name, String money_greeting, String moneyID, int chatType) {
//        Map<String, Object> attrs = new HashMap<String, Object>();
//        attrs.put(MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, isRedpacketMsg);
//        attrs.put(EXTRA_SPONSOR_NAME, money_sponsor_name);
//        attrs.put(EXTRA_RED_PACKET_GREETING, money_greeting);
//        attrs.put(EXTRA_RED_PACKET_ID, moneyID);
//        attrs.put(CHAT_TYPE, chatType);
//
//        return attrs;
//    }




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
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.toUserId = toUserId;
        redPacketInfo.chatType = chatType;
        redPacketInfo.toGroupId = toGroupId;
        redPacketInfo.groupMemberCount = groupMemberCount;
        redPacketInfo.chatType = chatType;
        return redPacketInfo;
    }

    public static RedPacketInfo initRedPacketInfo_received(String fromNickname, String fromAvatarUrl, String moneyMsgDirect, int chatType, String moneyId) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.moneyMsgDirect = moneyMsgDirect;
        redPacketInfo.chatType = chatType;
        redPacketInfo.moneyID = moneyId;
        redPacketInfo.toAvatarUrl = fromAvatarUrl;
        redPacketInfo.toNickName = fromNickname;
        return redPacketInfo;
    }

    public static boolean checkSendRPData(Map<String, Object> attrs) {
        //防止崩潰，先檢查數據
        if (attrs == null
                || !attrs.containsKey("redpacket")
                ) {
            return false;
        }
        return true;
    }

    public static boolean checkReceivedRPData(Map<String, Object> attrs) {
        //防止崩潰，先檢查數據
        if (!attrs.containsKey(RPConstant.EXTRA_LUCKY_MONEY_SENDER)
                || !attrs.containsKey(RPConstant.EXTRA_LUCKY_MONEY_RECEIVER)
                || !attrs.containsKey(RPConstant.EXTRA_LUCKY_MONEY_SENDER_ID)
                || !attrs.containsKey(RedPacketUtils.CHAT_TYPE)
                ) {
            return false;
        }
        return true;
    }


    public interface OnSuccessOpenRedPacket {

        void callBack(String content, int rpType, Map<String, Object> attrs);

    }







    public static   void selectRedPacket(Fragment fragment, String toUserId, String fromNickname, String fromAvatarUrl, int chatType, String tpGroupId, int membersNum, int REQUEST_CODE_SEND_MONEY) {
        Intent intent = new Intent(fragment.getActivity(), RPRedPacketActivity.class);
        //接收者Id或者接收的群Id
        RedPacketInfo redpacketInfo;
        if (chatType == 1) {
            redpacketInfo = initRedPacketInfo_single(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_SINGLE);
        } else if (chatType == 2) {
            redpacketInfo = initRedPacketInfo_group(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_GROUP, tpGroupId, membersNum);
        } else {
            return;
        }
        intent.putExtra(RPConstant.EXTRA_MONEY_INFO, redpacketInfo);
        fragment.startActivityForResult(intent, REQUEST_CODE_SEND_MONEY);
    }

}
