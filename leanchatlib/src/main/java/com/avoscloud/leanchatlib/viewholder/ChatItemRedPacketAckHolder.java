package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.easemob.redpacketsdk.constant.RPConstant;

import utils.RedPacketUtils;

public class ChatItemRedPacketAckHolder extends ChatItemHolder {

    protected TextView contentView;

    public ChatItemRedPacketAckHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        conventLayout.addView(View.inflate(getContext(), R.layout.lc_chat_item_redpacket_ack, null));
        avatarView.setVisibility(View.GONE);
        contentView = (TextView) itemView.findViewById(R.id.tv_money_msg);
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        nameView.setText("");
        AVIMMessage message = (AVIMMessage) o;
        String content = message.getContent();
        if (!TextUtils.isEmpty(content)) {
            JSONObject jsonObject = JSONObject.parseObject(content);
            if (jsonObject != null && jsonObject.containsKey("redpacket")) {
                JSONObject rpJSON = jsonObject.getJSONObject("redpacket");
                int chatType = RPConstant.CHATTYPE_SINGLE;
                if (ConversationHelper.typeOfConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(message.getConversationId())) == ConversationType.Group) {
                    chatType = RPConstant.CHATTYPE_GROUP;
                }
                ChatManager chatManager = ChatManager.getInstance();
                String selfId = chatManager.getSelfId();
                boolean isSend = message.getFrom() != null && message.getFrom().equals(selfId);
                initRedPacketAckChatItem(rpJSON, isSend, selfId, contentView, getContext(), chatType);
            }
        }
    }

    public void initRedPacketAckChatItem(JSONObject rpJSON, boolean isSend, String selfId, TextView contentView, Context context, int chatType) {
        String fromUser = rpJSON.getString(RedPacketUtils.EXTRA_RED_PACKET_SENDER_NAME);/*红包发送者*/
        String toUser = rpJSON.getString(RedPacketUtils.EXTRA_RED_PACKET_RECEIVER_NAME);/*红包接收者*/
        String senderId = rpJSON.getString(RedPacketUtils.EXTRA_RED_PACKET_SENDER_ID);
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
}