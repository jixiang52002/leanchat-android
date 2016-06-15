package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.activity.AVChatActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;

import java.util.Map;

import utils.RedPacketUtils;

public class ChatItemRedPacketHolder extends ChatItemHolder {

    protected TextView mTvGreeting;

    protected TextView mTvSponsorName;

    protected RelativeLayout mRedPacketLayout;

    public ChatItemRedPacketHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.lc_chat_item_left_text_redpacket_layout, null));
        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.lc_chat_item_right_text_redpacket_layout, null));
        }
        //红包view
        mRedPacketLayout = (RelativeLayout) itemView.findViewById(R.id.red_packet_layout);
        mTvGreeting = (TextView) itemView.findViewById(R.id.tv_money_greeting);
        mTvSponsorName = (TextView) itemView.findViewById(R.id.tv_sponsor_name);
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        AVIMMessage message = (AVIMMessage) o;
        if (message instanceof AVIMTextMessage) {
            AVIMTextMessage textMessage = (AVIMTextMessage) message;
            //获取附加字段
            Map<String, Object> attrs = textMessage.getAttrs();
            String fromNickname = getFromNickname();
            String fromAvatarUrl = getFromAvatarUrl();
            boolean isSend = textMessage.getFrom() != null && textMessage.getFrom().equals(selfId);
            RedPacketUtils.initRedPacketChatItem(attrs, mTvGreeting, mTvSponsorName, mRedPacketLayout, isSend, fromNickname, fromAvatarUrl, getContext(), new RedPacketUtils.OpenRedPacketCallback() {
                @Override
                public void onSuccess(String content, boolean isRP, Map<String, Object> attrs_temp) {
                    ((AVChatActivity) getContext()).chatFragment.sendText(content, isRP, attrs_temp);
                }
            });
        }
    }

    //获取本地用户的昵称和头像
    //先获取ID
    ChatManager chatManager = ChatManager.getInstance();

    String selfId = chatManager.getSelfId();

    private String getFromNickname() {
        //获取昵称
        String username = ThirdPartUserUtils.getInstance().getUserName(selfId);
        return TextUtils.isEmpty(username) ? selfId : username;
    }

    private String getFromAvatarUrl() {
        //获取头像
        String avatarUrl = ThirdPartUserUtils.getInstance().getUserAvatar(selfId);
        return TextUtils.isEmpty(avatarUrl) ? "none" : avatarUrl;
    }
}
