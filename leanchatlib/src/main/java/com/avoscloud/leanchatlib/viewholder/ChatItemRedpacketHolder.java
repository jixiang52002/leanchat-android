package com.avoscloud.leanchatlib.viewholder;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.activity.AVChatActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;

import java.util.Map;

import utils.RedPacketUtils;

/**
 * Created by wli on 15/9/17.
 */
public class ChatItemRedpacketHolder extends ChatItemHolder {

    protected TextView mTvGreeting;
    protected TextView mTvSponsorName;
    protected RelativeLayout re_bubble;
    protected Context context;


    public ChatItemRedpacketHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);


    }

    @Override
    public void initView() {
        super.initView();

        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_text_redpacket_layout, null));

        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_right_text_redpacket_layout, null));
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
            final AVIMTextMessage textMessage = (AVIMTextMessage) message;
            //获取附加字段
            final Map<String, Object> attrs = textMessage.getAttrs();
            //检查数据，防止解析崩溃
            if (!RedPacketUtils.checkSendRPData(attrs)) return;
            /**
             *
             *
             * UI
             */
            String sponsorName = (String) attrs.get(RedPacketUtils.EXTRA_SPONSOR_NAME);
            String greetings = (String) attrs.get(RedPacketUtils.EXTRA_RED_PACKET_GREETING);
            //设置红包信息
            mTvGreeting.setText(greetings);
            mTvSponsorName.setText(sponsorName);
            String moneyMsgDirect = getDirct(textMessage);
            //获取红包id
            String moneyId = (String) attrs.get(RedPacketUtils.EXTRA_RED_PACKET_ID);
            //获取聊天类型-----1单聊，2群聊--从附加字段里获取
            int chatType_temp;
            try {
                chatType_temp = (int) attrs.get(RedPacketUtils.CHAT_TYPE);
            } catch (Exception e) {
                chatType_temp = 1;
            }
            final int chatType = chatType_temp;
            final String fromNickname = getfromNickname();
            final String fromAvatarUrl = getfromAvatarUrl();
            final RedPacketInfo redPacketInfo = RedPacketUtils.initRedPacketInfo_received(fromNickname, fromAvatarUrl, moneyMsgDirect, chatType, moneyId);
            //红包点击
            re_bubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCanceledOnTouchOutside(false);
                    RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, (AVChatActivity) getContext(), new RPOpenPacketUtil.RPOpenPacketCallBack() {
                        @Override
                        public void onSuccess(String senderId, String senderNickname) {
                            String content = String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money), fromNickname);
                            final Map<String, Object> attrs_temp = RedPacketUtils.initReceivedRedPacketAttrs(true, fromNickname, senderNickname, senderId, chatType);
                            ((AVChatActivity) getContext()).chatFragment.sendText(content, true, attrs_temp);
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
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private String getDirct(AVIMTextMessage textMessage) {
        String moneyMsgDirect = "";
        //判断发送还是接收
        if (fromMe(textMessage)) {
            moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_SEND;

        } else {
            moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_RECEIVE;

        }
        return moneyMsgDirect;
    }

    private boolean fromMe(AVIMTypedMessage msg) {
        ChatManager chatManager = ChatManager.getInstance();
        String selfId = chatManager.getSelfId();

        return msg.getFrom() != null && msg.getFrom().equals(selfId);
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
