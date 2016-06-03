package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;

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
        if (message instanceof AVIMTextMessage) {
            final AVIMTextMessage textMessage = (AVIMTextMessage) message;
            //获取附加字段
            final Map<String, Object> attrs = textMessage.getAttrs();
            ChatManager chatManager = ChatManager.getInstance();
            String selfId = chatManager.getSelfId();
            boolean isSend = textMessage.getFrom() != null && textMessage.getFrom().equals(selfId);
            RedPacketUtils.initReceivedRedPacketChatItem(attrs, isSend, selfId, contentView, getContext());
        }
    }

}
