package com.avoscloud.leanchatlib.viewholder;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.activity.AVChatActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;

import java.util.HashMap;
import java.util.Map;

import utils.RedPacketUtils;
import utils.UserUtils;

/**
 * Created by wli on 15/9/17.
 */
public class RedPacketChatItemHolder extends ChatItemHolder {
    protected TextView mTvGreeting;
    protected TextView mTvSponsorName;
    protected RelativeLayout re_bubble;
    protected Context context;

    public RedPacketChatItemHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft)
            conventLayout.addView(View.inflate(getContext(), R.layout.rp_chat_item_left_text_redpacket_layout, null));
        else
            conventLayout.addView(View.inflate(getContext(), R.layout.rp_chat_item_right_text_redpacket_layout, null)); /*红包view*/
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
            int chatType = 1;
            if (ConversationHelper.typeOfConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(textMessage.getConversationId())) == ConversationType.Group)
                chatType = 2; /*获取附加字段*/
            Map<String, Object> attrs = textMessage.getAttrs();
            if (attrs == null || !attrs.containsKey("redpacket") || !(attrs.get("redpacket") instanceof com.alibaba.fastjson.JSONObject))
                return;
            JSONObject rpJSON = (JSONObject) attrs.get("redpacket");
            if (rpJSON.size() == 0) return;
            String fromNickname = UserUtils.getInstance(getContext()).getUserInfo("fromNickname");
            String fromAvatarUrl = UserUtils.getInstance(getContext()).getUserInfo("fromAvatarUrl");
            if (TextUtils.isEmpty(fromNickname)) fromNickname = getFromNickname();
            if (TextUtils.isEmpty(fromAvatarUrl)) fromAvatarUrl = getFromAvatarUrl();
            boolean isSend = textMessage.getFrom() != null && textMessage.getFrom().equals(selfId);
            initRedPacketChatItem(rpJSON, chatType, mTvGreeting, mTvSponsorName, re_bubble, isSend, fromNickname, fromAvatarUrl, selfId, getContext());
        }
    } /*获取本地用户的昵称和头像 先获取ID*/

    ChatManager chatManager = ChatManager.getInstance();
    String selfId = chatManager.getSelfId();

    private String getFromNickname() { /*获取昵称*/
        String username = ThirdPartUserUtils.getInstance().getUserName(selfId);
        String fromNickname = TextUtils.isEmpty(username) ? selfId : username;
        return fromNickname;
    }

    private String getFromAvatarUrl() { /*获取头像*/
        String avatarUrl = ThirdPartUserUtils.getInstance().getUserAvatar(selfId);
        final String fromAvatarUrl = TextUtils.isEmpty(avatarUrl) ? "none" : avatarUrl;
        return fromAvatarUrl;
    }

    public void initRedPacketChatItem(JSONObject rpJSON, final int chatType, TextView mTvGreeting, TextView mTvSponsorName, RelativeLayout re_bubble, boolean isSend, final String fromNickname, String fromAvatarUrl, final String fromUserId, final Context context) {
        final String ID = rpJSON.getString("ID");
        final String money_greeting = rpJSON.getString("money_greeting");
        final String money_sponsor_name = rpJSON.getString("money_sponsor_name");
        mTvGreeting.setText(money_greeting);
        mTvSponsorName.setText(money_sponsor_name);
        String moneyMsgDirect; /*判断发送还是接收*/
        if (isSend) moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_SEND;
        else moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_RECEIVE;
        final RedPacketInfo redPacketInfo = RedPacketUtils.initRedPacketInfo_received(fromNickname, fromAvatarUrl, moneyMsgDirect, chatType, ID); /*红包点击*/
        re_bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setCanceledOnTouchOutside(false);
                RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, (FragmentActivity) context, new RPOpenPacketUtil.RPOpenPacketCallBack() {
                    @Override
                    public void onSuccess(String senderId, String senderNickname) {
                        String content = String.format(context.getResources().getString(R.string.money_msg_someone_take_money), fromNickname);
                        final JSONObject jsonObject = initReceivedRedPacketAttrs(ID, money_greeting, money_sponsor_name, fromNickname, fromUserId, senderNickname, senderId);
                        ((AVChatActivity) context).chatFragment.sendText(content, jsonObject);
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
                    public void onError(String code, String message) { /*错误处理*/
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 设置领取红包后发领取通知的附加字段的attrs
     */
    public JSONObject initReceivedRedPacketAttrs(String ID, String money_greeting, String money_sponsor_name, String money_receiver, String money_receiver_id, String senderNickname, String senderId) {
        JSONObject jsonObject = new JSONObject();
        JSONObject rpJSON = new JSONObject();
        JSONObject userJSON = new JSONObject();
        rpJSON.put("money_sender", senderNickname);
        rpJSON.put("ID", ID);
        rpJSON.put("is_open_money_msg", true);
        rpJSON.put("money_greeting", money_greeting);
        rpJSON.put("money_receiver", money_receiver);
        rpJSON.put("money_receiver_id", money_receiver_id);
        rpJSON.put("money_sender_id", senderId);
        rpJSON.put("money_sponsor_name", money_sponsor_name);
        userJSON.put("id", money_receiver_id);
        userJSON.put("username", money_receiver);
        jsonObject.put("redpacket", rpJSON);
        jsonObject.put("redpacket_user", userJSON);
        jsonObject.put("type", "redpacket_taken");
        return jsonObject;
    }
}
