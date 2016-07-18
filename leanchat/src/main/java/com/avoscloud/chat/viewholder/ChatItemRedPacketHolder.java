package com.avoscloud.chat.viewholder;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avoscloud.chat.R;
import com.avoscloud.chat.event.RedPacketAckEvent;
import com.avoscloud.chat.model.LCIMRedPacketMessage;
import com.avoscloud.chat.model.LCIMRedPcketAckMessage;
import com.avoscloud.chat.model.LeanchatUser;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketui.utils.RPOpenPacketUtil;

import cn.leancloud.chatkit.viewholder.LCIMChatItemHolder;
import de.greenrobot.event.EventBus;
import utils.RedPacketUtils;

/**
 * 点击红包消息，领取红包或者查看红包详情
 */
public class ChatItemRedPacketHolder extends LCIMChatItemHolder {

  protected TextView mTvGreeting;

  protected TextView mTvSponsorName;

  protected RelativeLayout mRedPacketLayout;

  LCIMRedPacketMessage redPacketMessage;

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

    mRedPacketLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != redPacketMessage) {
          openRedPacket(getContext(), redPacketMessage);
        }
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    AVIMMessage message = (AVIMMessage) o;
    if (message instanceof LCIMRedPacketMessage) {
      redPacketMessage = (LCIMRedPacketMessage) message;
      mTvGreeting.setText(redPacketMessage.getGreeting());
      mTvSponsorName.setText(redPacketMessage.getSponsorName());
    }
  }

  private void openRedPacket(final Context context, final LCIMRedPacketMessage message) {
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setCanceledOnTouchOutside(false);

    final String selfName = LeanchatUser.getCurrentUser().getUsername();
    String selfAvatar = LeanchatUser.getCurrentUser().getAvatarUrl();
    final String selfId = LeanchatUser.getCurrentUserId();
    String moneyMsgDirect; /*判断发送还是接收*/
    if (message.getFrom() != null && message.getFrom().equals(selfId)) {
      moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_SEND;
    } else {
      moneyMsgDirect = RedPacketUtils.MESSAGE_DIRECT_RECEIVE;
    }

    final RedPacketInfo redPacketInfo = RedPacketUtils.initRedPacketInfo_received(selfId, selfAvatar, moneyMsgDirect, 1, message.getReadPacketId());
    RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, (FragmentActivity) context, new RPOpenPacketUtil.RPOpenPacketCallBack() {
      @Override
      public void onSuccess(String senderId, String senderNickname) {
        LCIMRedPcketAckMessage ackMessage = new LCIMRedPcketAckMessage();
        ackMessage.setSenderId(senderId);
        ackMessage.setSenderName(senderNickname);
        ackMessage.setRecipientId(selfId);
        ackMessage.setRecipientName(selfName);
        ackMessage.setSingle(message.isSingle());
        EventBus.getDefault().post(new RedPacketAckEvent(ackMessage));
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
}
