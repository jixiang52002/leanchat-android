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
import com.yunzhanghu.redpacketsdk.bean.RPUserBean;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.utils.RPOpenPacketUtil;

import java.util.Map;

import utils.RedPacketUtils;
import utils.UserBeanCallback;
import utils.UserUtils;

/**
 * 点击红包消息，领取红包或者查看红包详情
 */
public class ChatItemRedPacketHolder extends ChatItemHolder {

    protected TextView mTvGreeting;

    protected TextView mTvSponsorName;

    protected TextView mTvPacketType;

    protected RelativeLayout mRedPacketLayout;

    protected RPUserBean rpUserBean;

    public ChatItemRedPacketHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        if (isLeft) {
            conventLayout.addView(View.inflate(getContext(), R.layout.lc_chat_item_left_text_redpacket_layout, null));
        } else {
            conventLayout.addView(View.inflate(getContext(), R.layout.lc_chat_item_right_text_redpacket_layout, null)); /*红包view*/
        }
        mRedPacketLayout = (RelativeLayout) itemView.findViewById(R.id.red_packet_layout);
        mTvGreeting = (TextView) itemView.findViewById(R.id.tv_money_greeting);
        mTvSponsorName = (TextView) itemView.findViewById(R.id.tv_sponsor_name);
        mTvPacketType = (TextView) itemView.findViewById(R.id.tv_packet_type);
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        AVIMMessage message = (AVIMMessage) o;
        if (message instanceof AVIMTextMessage) {
            AVIMTextMessage textMessage = (AVIMTextMessage) message;
            int chatType = 1;
            if (ConversationHelper.typeOfConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(textMessage.getConversationId())) == ConversationType.Group) {
                chatType = 2; /*获取附加字段*/
            }
            Map<String, Object> attrs = textMessage.getAttrs();
            if (attrs == null || !attrs.containsKey(RedPacketUtils.KEY_RED_PACKET) || !(attrs.get(RedPacketUtils.KEY_RED_PACKET) instanceof com.alibaba.fastjson.JSONObject)) {
                return;
            }
            JSONObject rpJSON = (JSONObject) attrs.get(RedPacketUtils.KEY_RED_PACKET);
            if (rpJSON.size() == 0) {
                return;
            }
            String fromNickname = UserUtils.getInstance(getContext()).getUserInfo(UserUtils.USER_NICK_NAME);
            String fromAvatarUrl = UserUtils.getInstance(getContext()).getUserInfo(UserUtils.USER_AVATAR_URL);
            if (TextUtils.isEmpty(fromNickname)) {
                fromNickname = getFromNickname();
            }
            if (TextUtils.isEmpty(fromAvatarUrl)) {
                fromAvatarUrl = getFromAvatarUrl();
            }

            boolean isSend = textMessage.getFrom() != null && textMessage.getFrom().equals(selfId);
            initRedPacketChatItem(rpJSON, chatType, mTvGreeting, mTvSponsorName, mRedPacketLayout, isSend, fromNickname, fromAvatarUrl, selfId, getContext());
        }
    } /*获取本地用户的昵称和头像 先获取ID*/

    ChatManager chatManager = ChatManager.getInstance();

    String selfId = chatManager.getSelfId();

    private String getFromNickname() { /*获取昵称*/
        String username = ThirdPartUserUtils.getInstance().getUserName(selfId);
        return TextUtils.isEmpty(username) ? selfId : username;
    }

    private String getFromAvatarUrl() { /*获取头像*/
        String avatarUrl = ThirdPartUserUtils.getInstance().getUserAvatar(selfId);
        return TextUtils.isEmpty(avatarUrl) ? "none" : avatarUrl;
    }

    /**
     * Metohd name:
     * Describe: 点击拆红包或者是查看红包
     * Create person：侯洪旭
     * Create time：16/7/1 下午3:59
     * Remarks：
     */
    private void initRedPacketChatItem(JSONObject rpJSON, final int chatType, TextView mTvGreeting, TextView mTvSponsorName, RelativeLayout re_bubble, boolean isSend, final String fromNickname, String fromAvatarUrl, final String fromUserId, final Context context) {
        final String redPacketId = rpJSON.getString(RedPacketUtils.EXTRA_RED_PACKET_ID);
        final String greeting = rpJSON.getString(RedPacketUtils.EXTRA_RED_PACKET_GREETING);
        final String sponsorName = rpJSON.getString(RedPacketUtils.EXTRA_SPONSOR_NAME);
        final String redpackettype = rpJSON.getString(RedPacketUtils.KEY_RED_PACKET_TYPE);

        mTvGreeting.setText(greeting);
        mTvSponsorName.setText(sponsorName);
        String moneyMsgDirect; /*判断发送还是接收*/
        if (isSend) moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_SEND;
        else moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_RECEIVE;
        final RedPacketInfo redPacketInfo;
        /**
         * 专属红包
         */
        if (!TextUtils.isEmpty(redpackettype) && redpackettype.equals(RPConstant.GROUP_RED_PACKET_TYPE_EXCLUSIVE)) {
            /**
             * 专属红包时获取,接收专属红包人的信息,姓名和头像,需要传给红包SDK
             */
            RedPacketUtils.getInstance().getmGetUserBeanCallback().done(rpJSON.getString(RedPacketUtils.KEY_RED_PACKET_SPECIAL_RECEIVEID), new UserBeanCallback() {
                @Override
                public void getUserInfo(RPUserBean userbean) {
                    rpUserBean = userbean;
                }
            });
            /**
             * 封装打开专属红包时所要传的参数
             */
            redPacketInfo = RedPacketUtils.initRedPacketInfo_received(rpUserBean, fromNickname, fromAvatarUrl, moneyMsgDirect, chatType, redPacketId); /*红包点击*/
            mTvPacketType.setVisibility(View.VISIBLE);
            mTvPacketType.setText(context.getResources().getString(R.string.exclusive_red_envelope));
        } else {//普通红包
            /**
             * 封装打开普通红包时所要传的参数
             */
            redPacketInfo = RedPacketUtils.initRedPacketInfo_received(fromNickname, fromAvatarUrl, moneyMsgDirect, chatType, redPacketId); /*红包点击*/
            mTvPacketType.setVisibility(View.GONE);
        }
        /**
         * 点击事件
         */
        onclick(re_bubble, redPacketInfo, context, redPacketId, greeting, sponsorName, fromNickname, fromUserId);
    }

    /**
     * Metohd name:
     * Describe: 红包点击事件
     * Create person：侯洪旭
     * Create time：16/7/1 下午4:08
     * Remarks：
     */
    private void onclick(RelativeLayout re_bubble, final RedPacketInfo redPacketInfo, final Context context, final String redPacketId, final String greeting, final String sponsorName, final String fromNickname, final String fromUserId) {
        /**
         * 点击红包进行拆除或者是查看
         */
        re_bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setCanceledOnTouchOutside(false);
                /**
                 * 打开红包
                 */
                RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, RedPacketUtils.getInstance().getmAuthData(), (FragmentActivity) context, new RPOpenPacketUtil.RPOpenPacketCallBack() {
                    @Override
                    public void onSuccess(String senderId, String senderNickname) {
                        String content = String.format(context.getResources().getString(R.string.money_msg_someone_take_money), fromNickname);
                        final JSONObject jsonObject = initRedPacketAckAttrs(redPacketId, greeting, sponsorName, fromNickname, fromUserId, senderNickname, senderId);
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
    private JSONObject initRedPacketAckAttrs(String redPacketId, String greetings, String sponsorName, String receiverNickname, String receiverId, String senderNickname, String senderId) {
        JSONObject jsonObject = new JSONObject();
        JSONObject rpJSON = new JSONObject();
        JSONObject userJSON = new JSONObject();
        rpJSON.put(RedPacketUtils.EXTRA_RED_PACKET_ID, redPacketId);
        rpJSON.put(RedPacketUtils.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
        rpJSON.put(RedPacketUtils.EXTRA_RED_PACKET_GREETING, greetings);
        rpJSON.put(RedPacketUtils.EXTRA_RED_PACKET_RECEIVER_ID, receiverId);
        rpJSON.put(RedPacketUtils.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
        rpJSON.put(RedPacketUtils.EXTRA_RED_PACKET_SENDER_ID, senderId);
        rpJSON.put(RedPacketUtils.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
        rpJSON.put(RedPacketUtils.EXTRA_SPONSOR_NAME, sponsorName);
        userJSON.put(RedPacketUtils.KEY_USER_ID, receiverId);
        userJSON.put(RedPacketUtils.KEY_USER_NAME, receiverNickname);
        jsonObject.put(RedPacketUtils.KEY_RED_PACKET, rpJSON);
        jsonObject.put(RedPacketUtils.KEY_RED_PACKET_USER, userJSON);
        jsonObject.put(RedPacketUtils.KEY_TYPE, RedPacketUtils.VALUE_TYPE);
        return jsonObject;
    }
}
