package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;

import java.util.Map;

import utils.RedPacketUtils;

/**
 * Created by ustc on 2016/5/30.
 */
public class ReceivedRedPacketChatItemHolder extends ChatItemHolder {
    protected TextView contentView;

    public ReceivedRedPacketChatItemHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        conventLayout.addView(View.inflate(getContext(), R.layout.rp_chat_item_received_redpacket, null));
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
                int chatType = 1;
                if (ConversationHelper.typeOfConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(message.getConversationId())) == ConversationType.Group)
                    chatType = 2; /*  final Map<String, Object> attrs = textMessage.getAttrs();*/
                ChatManager chatManager = ChatManager.getInstance();
                String selfId = chatManager.getSelfId();
                boolean isSend = message.getFrom() != null && message.getFrom().equals(selfId);
                initReceivedRedPacketChatItem(rpJSON, isSend, selfId, contentView, getContext(), chatType);
            }
        }
    }

    public void initReceivedRedPacketChatItem(JSONObject rpJSON, boolean isSend, String selfId, TextView contentView, Context context, int chatType) {
        String fromUser = rpJSON.getString("money_sender");/*红包发送者*/
        String toUser = rpJSON.getString("money_receiver");/*红包接收者*/
        String senderId = rpJSON.getString("money_sender_id");
        if (isSend) if (chatType == 2)
            if (senderId.equals(selfId)) contentView.setText(R.string.money_msg_take_money);
            else
                contentView.setText(String.format(context.getResources().getString(R.string.money_msg_take_someone_money), fromUser));
        else
            contentView.setText(String.format(context.getResources().getString(R.string.money_msg_take_someone_money), fromUser));
        else if (senderId.equals(selfId))
            contentView.setText(String.format(context.getResources().getString(R.string.money_msg_someone_take_money), toUser));
        else
            contentView.setText(String.format(context.getResources().getString(R.string.money_msg_someone_take_money_same), toUser, fromUser));
    }
}