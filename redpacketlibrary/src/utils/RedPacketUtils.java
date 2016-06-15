package utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketui.R;
import com.easemob.redpacketui.ui.activity.RPRedPacketActivity;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;

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
    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String MESSAGE_DIRECT_SEND = "SEND";
    public static final String MESSAGE_DIRECT_RECEIVE = "RECEIVE";

    /**
     * 设置发消息红包的附加字段的attrs
     **/
    public static Map<String, Object> initSendRedPacketAttrs(boolean isRedPacketMsg, String money_sponsor_name, String money_greeting, String moneyID, int chatType) {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, isRedPacketMsg);
        attrs.put(EXTRA_SPONSOR_NAME, money_sponsor_name);
        attrs.put(EXTRA_RED_PACKET_GREETING, money_greeting);
        attrs.put(EXTRA_RED_PACKET_ID, moneyID);
        attrs.put(EXTRA_CHAT_TYPE, chatType);
        return attrs;
    }

    /**
     * 设置领取红包后发领取通知的附加字段的attrs
     **/
    public static Map<String, Object> initRedPacketAckAttrs(boolean isRedPacketAckMsg, String fromNickname, String senderNickname, String senderId, int chatType) {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, isRedPacketAckMsg);
        attrs.put(EXTRA_RED_PACKET_RECEIVER_NAME, fromNickname);
        attrs.put(EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
        attrs.put(EXTRA_RED_PACKET_SENDER_ID, senderId);
        attrs.put(EXTRA_CHAT_TYPE, chatType);
        return attrs;
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
                || !attrs.containsKey(EXTRA_SPONSOR_NAME)
                || !attrs.containsKey(EXTRA_RED_PACKET_GREETING)
                || !attrs.containsKey(EXTRA_RED_PACKET_ID)
                || !attrs.containsKey(EXTRA_CHAT_TYPE)
                ) {
            return false;
        }
        return true;
    }

    public static boolean checkReceivedRPData(Map<String, Object> attrs) {
        //防止崩潰，先檢查數據
        if (!attrs.containsKey(EXTRA_RED_PACKET_SENDER_NAME)
                || !attrs.containsKey(EXTRA_RED_PACKET_RECEIVER_NAME)
                || !attrs.containsKey(EXTRA_RED_PACKET_SENDER_ID)
                || !attrs.containsKey(EXTRA_CHAT_TYPE)
                ) {
            return false;
        }
        return true;
    }

    public static void initRedPacketChatItem(Map<String, Object> attrs, TextView mTvGreeting, TextView mTvSponsorName, RelativeLayout re_bubble, boolean isSend, final String fromNickname, String fromAvatarUrl, final Context context, final OpenRedPacketCallback openRedPacketCallback) {
        //检查数据，防止解析崩溃
        if (!checkSendRPData(attrs)) return;
        //UI
        String sponsorName = (String) attrs.get(EXTRA_SPONSOR_NAME);
        String greetings = (String) attrs.get(EXTRA_RED_PACKET_GREETING);
        //设置红包信息
        mTvGreeting.setText(greetings);
        mTvSponsorName.setText(sponsorName);
        String moneyMsgDirect;
        //判断发送还是接收
        if (isSend) {
            moneyMsgDirect = MESSAGE_DIRECT_SEND;
        } else {
            moneyMsgDirect = MESSAGE_DIRECT_RECEIVE;
        }
        //获取红包id
        String moneyId = (String) attrs.get(EXTRA_RED_PACKET_ID);
        //获取聊天类型-----1单聊，2群聊--从附加字段里获取
        int chatType_temp = RPConstant.CHATTYPE_SINGLE;
        try {
            chatType_temp = (int) attrs.get(EXTRA_CHAT_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int chatType = chatType_temp;

        final RedPacketInfo redPacketInfo = initRedPacketInfo_received(fromNickname, fromAvatarUrl, moneyMsgDirect, chatType, moneyId);
        //红包点击
        re_bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setCanceledOnTouchOutside(false);
                RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, (FragmentActivity) context, new RPOpenPacketUtil.RPOpenPacketCallBack() {
                    @Override
                    public void onSuccess(String senderId, String senderNickname) {
                        String content = String.format(context.getResources().getString(R.string.money_msg_someone_take_money), fromNickname);
                        final Map<String, Object> attrs_temp = initRedPacketAckAttrs(true, fromNickname, senderNickname, senderId, chatType);
                        openRedPacketCallback.onSuccess(content, true, attrs_temp);
                    }

                    @Override
                    public void showLoading() {
                        progressDialog.show();
                    }

                    @Override
                    public void hideLoading() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(String code, String message) {
                        //错误处理
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    public interface OpenRedPacketCallback {

        void onSuccess(String content, boolean isRP, Map<String, Object> attrs);

    }


    public static void initRedPacketAckChatItem(Map<String, Object> attrs, boolean isSend, String selfId, TextView contentView, Context context) {
        //防止崩潰，先檢查數據
        if (!checkReceivedRPData(attrs)) return;
        String fromUser = (String) attrs.get(EXTRA_RED_PACKET_SENDER_NAME);//红包发送者昵称
        String toUser = (String) attrs.get(EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者昵称
        String senderId = (String) attrs.get(EXTRA_RED_PACKET_SENDER_ID);//发送者id
        //获取聊天类型-----1或者RPConstant.CHATTYPE_SINGLE单聊，2或者RPConstant.CHATTYPE_GROUP群聊--从附加字段里获取
        int chatType = RPConstant.CHATTYPE_SINGLE;
        try {
            chatType = (int) attrs.get(EXTRA_CHAT_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSend) {
            if (chatType == RPConstant.CHATTYPE_GROUP) {
                if (senderId.equals(selfId)) {
                    contentView.setText(R.string.money_msg_take_money);
                } else {
                    contentView.setText(String.format(context.getResources().getString(R.string.money_msg_take_someone_money), fromUser));
                }
            } else {
                contentView.setText(String.format(context.getResources().getString(R.string.money_msg_take_someone_money), fromUser));
            }
        } else {
            if (senderId.equals(selfId)) {
                contentView.setText(String.format(context.getResources().getString(R.string.money_msg_someone_take_money), toUser));
            } else {
                contentView.setText(String.format(context.getResources().getString(R.string.money_msg_someone_take_money_same), toUser, fromUser));
            }
        }
    }


    public static void selectRedPacket(Fragment fragment, String toUserId, String fromNickname, String fromAvatarUrl, int chatType, String tpGroupId, int membersNum, int REQUEST_CODE_SEND_MONEY) {
        Intent intent = new Intent(fragment.getActivity(), RPRedPacketActivity.class);
        //接收者Id或者接收的群Id
        RedPacketInfo redpacketInfo;
        if (chatType == RPConstant.CHATTYPE_SINGLE) {
            redpacketInfo = initRedPacketInfo_single(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_SINGLE);
        } else if (chatType == RPConstant.CHATTYPE_GROUP) {
            redpacketInfo = initRedPacketInfo_group(fromNickname, fromAvatarUrl, toUserId, RPConstant.CHATTYPE_GROUP, tpGroupId, membersNum);
        } else {
            return;
        }
        intent.putExtra(RPConstant.EXTRA_MONEY_INFO, redpacketInfo);
        fragment.startActivityForResult(intent, REQUEST_CODE_SEND_MONEY);
    }

}
