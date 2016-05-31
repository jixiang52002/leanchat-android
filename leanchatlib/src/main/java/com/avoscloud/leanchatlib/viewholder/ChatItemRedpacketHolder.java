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
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;

import java.util.HashMap;
import java.util.Map;

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

            //防止崩潰，先檢查數據
            if (!attrs.containsKey(RPConstant.EXTRA_SPONSOR_NAME)
                    || !attrs.containsKey(RPConstant.EXTRA_MONEY_GREETING)
                    || !attrs.containsKey(RPConstant.EXTRA_CHECK_MONEY_ID)
                    || !attrs.containsKey("chatType")

                    ) {

                return;
            }

            String sponsorName = (String) attrs.get(RPConstant.EXTRA_SPONSOR_NAME);
            String greetings = (String) attrs.get(RPConstant.EXTRA_MONEY_GREETING);
            //获取红包id
            final String moneyId = (String) attrs.get(RPConstant.EXTRA_CHECK_MONEY_ID);
            //获取聊天类型-----1单聊，2群聊--从附加字段里获取
            int chatType_temp;
            try {
                chatType_temp = (int) attrs.get("chatType");
            } catch (Exception e) {
                chatType_temp = 1;
            }
            final int chatType = chatType_temp;
            //获取本地用户的昵称和头像
            //先获取ID
            ChatManager chatManager = ChatManager.getInstance();
            String selfId = chatManager.getSelfId();
            //获取昵称
            String username = ThirdPartUserUtils.getInstance().getUserName(selfId);
            final String fromNickname = TextUtils.isEmpty(username) ? selfId : username;
            //获取头像
            String avatarUrl = ThirdPartUserUtils.getInstance().getUserAvatar(selfId);
            final String fromAvatarUrl = TextUtils.isEmpty(avatarUrl) ? "none" : avatarUrl;
            //设置红包信息
            mTvGreeting.setText(greetings);
            mTvSponsorName.setText(sponsorName);
            //红包点击
            re_bubble.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCanceledOnTouchOutside(false);

                    RedPacketInfo redPacketInfo = new RedPacketInfo();
                    //   System.out.println("rp---chatType------>>"+chatType);
                    //判断发送还是接收
                    if (fromMe(textMessage)) {
                        redPacketInfo.moneyMsgDirect = RPConstant.MESSAGE_DIRECT_SEND;

                    } else {
                        redPacketInfo.moneyMsgDirect = RPConstant.MESSAGE_DIRECT_RECEIVE;

                    }

                    if (chatType == 1) {
                        redPacketInfo.chatType = RPConstant.CHATTYPE_SINGLE;

                    } else {
                        redPacketInfo.chatType = RPConstant.CHATTYPE_GROUP;

                    }

                    redPacketInfo.moneyID = moneyId;
                    redPacketInfo.toAvatarUrl = fromAvatarUrl;
                    redPacketInfo.toNickName = fromNickname;
//                    System.out.println("rp---moneyId------>>" + moneyId);
//                    System.out.println("rp---moneyMsgDirect------>>" + redPacketInfo.moneyMsgDirect);
//                    System.out.println("rp---fromAvatarUrl------>>" + fromAvatarUrl);
//                    System.out.println("rp---fromNickname------>>" + fromNickname);

                    RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, (AVChatActivity) getContext(), new RPOpenPacketUtil.RPOpenPacketCallBack() {
                        @Override
                        public void onSuccess(String senderId, String senderNickname) {
                            String content = String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money), fromNickname);
                            final Map<String, Object> attrs_temp = new HashMap<String, Object>();
                            attrs_temp.put(RPConstant.MESSAGE_ATTR_IS_OPEN_MONEY_MESSAGE, true);
                            attrs_temp.put(RPConstant.EXTRA_LUCKY_MONEY_RECEIVER, fromNickname);
                            attrs_temp.put(RPConstant.EXTRA_LUCKY_MONEY_SENDER, senderNickname);
                            attrs_temp.put(RPConstant.EXTRA_LUCKY_MONEY_SENDER_ID, senderId);
                            attrs_temp.put("chatType", chatType);
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

    private boolean fromMe(AVIMTypedMessage msg) {
        ChatManager chatManager = ChatManager.getInstance();
        String selfId = chatManager.getSelfId();

        return msg.getFrom() != null && msg.getFrom().equals(selfId);
    }


}
