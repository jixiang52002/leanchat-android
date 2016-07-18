package com.avoscloud.chat.model;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

import cn.leancloud.chatkit.LCChatMessageInterface;
import utils.RedPacketUtils;

/**
 * Created by wli on 16/7/11.
 */

@AVIMMessageType(type = LCIMRedPacketMessage.PACKMESSAGE_TYPE)
public class LCIMRedPacketMessage extends AVIMTypedMessage implements LCChatMessageInterface {

  public static final int PACKMESSAGE_TYPE = 1001;

  public LCIMRedPacketMessage() {}

  @AVIMMessageField(name = RedPacketUtils.EXTRA_RED_PACKET_ID)
  private String readPacketId;

  @AVIMMessageField(name = RedPacketUtils.EXTRA_RED_PACKET_GREETING)
  private String greeting;

  @AVIMMessageField(name = RedPacketUtils.EXTRA_SPONSOR_NAME)
  private String sponsorName;

  /**
   * 消息发送时是否是单聊
   */
  @AVIMMessageField(name = "isSingle")
  private boolean isSingle;

  public static final Creator<LCIMRedPacketMessage> CREATOR = new AVIMMessageCreator<LCIMRedPacketMessage>(LCIMRedPacketMessage.class);

  @Override
  public String getShorthand() {
    return "[LeanCloud红包]" + greeting;
  }

  public String getReadPacketId() {
    return readPacketId;
  }

  public void setReadPacketId(String readPacketId) {
    this.readPacketId = readPacketId;
  }

  public String getSponsorName() {
    return sponsorName;
  }

  public void setSponsorName(String sponsorName) {
    this.sponsorName = sponsorName;
  }

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  public boolean isSingle() {
    return isSingle;
  }

  public void setSingle(boolean single) {
    isSingle = single;
  }
}