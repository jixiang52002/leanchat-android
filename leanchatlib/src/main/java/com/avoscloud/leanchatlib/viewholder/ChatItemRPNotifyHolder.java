package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.activity.AVChatActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.event.ImTypeMessageResendEvent;
import com.avoscloud.leanchatlib.event.LeftChatItemClickEvent;
import com.avoscloud.leanchatlib.event.LocationItemClickEvent;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.avoscloud.leanchatlib.utils.Utils;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by ustc on 2016/5/30.
 */
public class ChatItemRPNotifyHolder extends ChatItemHolder {

    protected TextView contentView;

    public ChatItemRPNotifyHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_money_message, null));
        avatarView.setVisibility(View.GONE);

        contentView = (TextView) itemView.findViewById(R.id.tv_money_msg);


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
            if(
           !attrs.containsKey(RPConstant.EXTRA_LUCKY_MONEY_SENDER)
                    ||!attrs.containsKey(RPConstant.EXTRA_LUCKY_MONEY_RECEIVER)
                   ||!attrs.containsKey(RPConstant.EXTRA_LUCKY_MONEY_SENDER_ID)
                    ||!attrs.containsKey("chatType")

                    ){

                   return;
            }




            String fromUser= (String) attrs.get(RPConstant.EXTRA_LUCKY_MONEY_SENDER);//红包发送者
            String toUser= (String) attrs.get(RPConstant.EXTRA_LUCKY_MONEY_RECEIVER);//红包接收者
            String senderId =(String) attrs.get(RPConstant.EXTRA_LUCKY_MONEY_SENDER_ID);

            //获取聊天类型-----1单聊，2群聊--从附加字段里获取
            int chatType = 1;
            try {
                chatType = (int) attrs.get("chatType");
            } catch (Exception e) {
                chatType = 1;
            }



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






            if (fromMe(textMessage)) {
                if (chatType==2) {

                    if (senderId.equals(selfId)) {
                        contentView.setText(R.string.money_msg_take_money);
                    } else {
                        contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_take_someone_money), fromUser));
                    }
                } else {
                    contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_take_someone_money), fromUser));
                }
            } else {
                if(senderId.equals(selfId)){
                    contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money), toUser));

                }else{
                    contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money_same), toUser,fromUser));
                }


            }

        }
    }


    private boolean fromMe(AVIMTypedMessage msg) {
        ChatManager chatManager = ChatManager.getInstance();
        String selfId = chatManager.getSelfId();

        return msg.getFrom() != null && msg.getFrom().equals(selfId);
    }
}
