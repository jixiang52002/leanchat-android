package com.avoscloud.chat.model;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

import cn.leancloud.chatkit.LCChatMessageInterface;
import utils.RedPacketUtils;

/**
 * Created by wli on 16/7/14.
 * 红包发送被别人接收后的 tip message
 */
@AVIMMessageType(type = LCIMRedPcketAckMessage.RED_PACKET_ACK_MESSAGE_TYPE)
public class LCIMRedPcketAckMessage extends AVIMTypedMessage implements LCChatMessageInterface {

  public LCIMRedPcketAckMessage() {}

  public static final Creator<LCIMRedPcketAckMessage> CREATOR = new AVIMMessageCreator<LCIMRedPcketAckMessage>(LCIMRedPcketAckMessage.class);

  public static final int RED_PACKET_ACK_MESSAGE_TYPE = 1002;

  /**
   * 红包的发送者 id
   */
  @AVIMMessageField(name = RedPacketUtils.EXTRA_RED_PACKET_SENDER_ID)
  private String senderId;

  /**
   * 红包的发送者 name
   */
  @AVIMMessageField(name = RedPacketUtils.EXTRA_RED_PACKET_SENDER_NAME)
  private String senderName;

  /**
   * 红包的接收者 id
   */
  @AVIMMessageField(name = "recipient_id")
  private String recipientId;

  /**
   * 红包的接收者 name
   */
  @AVIMMessageField(name = "recipient_name")
  private String recipientName;

  /**
   * 消息发送时是否是单聊
   */
  @AVIMMessageField(name = "isSingle")
  private boolean isSingle;

  @Override
  public String getShorthand() {
    return null;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public String getRecipientName() {
    return recipientName;
  }

  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }

  public boolean isSingle() {
    return isSingle;
  }

  public void setSingle(boolean single) {
    isSingle = single;
  }
}
