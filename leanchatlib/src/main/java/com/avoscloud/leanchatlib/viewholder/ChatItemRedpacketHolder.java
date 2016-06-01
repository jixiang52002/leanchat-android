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

/**
 * Created by wli on 15/9/17.
 */
public class ChatItemRedPacketHolder extends ChatItemHolder {

    protected TextView mTvGreeting;
    protected TextView mTvSponsorName;
    protected RelativeLayout re_bubble;
    protected Context context;


    public ChatItemRedPacketHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);


    }

    @Override
    public void initView() {
        super.initView();

        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.rp_chat_item_left_text_redpacket_layout, null));

        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.rp_chat_item_right_text_redpacket_layout, null));
        }

        re_bubble = (RelativeLayout) itemView.findViewById(R.id.bubble);
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
            String fromNickname = getfromNickname();
            String fromAvatarUrl = getfromAvatarUrl();
            boolean isSend= textMessage.getFrom() != null && textMessage.getFrom().equals(selfId);
            RedPacketUtils.initRedpacketChatItem(attrs, mTvGreeting, mTvSponsorName, re_bubble, isSend , fromNickname, fromAvatarUrl, getContext(), new RedPacketUtils.OnSuccessOpenRedPacket() {
                @Override
                public void callBack(String content, boolean isRP, Map<String, Object> attrs_temp) {
                    ((AVChatActivity) getContext()).chatFragment.sendText(content, true, attrs_temp);
                }
            });
        }

    }






    //获取本地用户的昵称和头像
    //先获取ID
    ChatManager chatManager = ChatManager.getInstance();
    String selfId = chatManager.getSelfId();

    private String getfromNickname() {
        //获取昵称
        String username = ThirdPartUserUtils.getInstance().getUserName(selfId);
        String fromNickname = TextUtils.isEmpty(username) ? selfId : username;
        return fromNickname;
    }

    private String getfromAvatarUrl() {
        //获取头像
        String avatarUrl = ThirdPartUserUtils.getInstance().getUserAvatar(selfId);
        final String fromAvatarUrl = TextUtils.isEmpty(avatarUrl) ? "none" : avatarUrl;
        return fromAvatarUrl;
    }


}
