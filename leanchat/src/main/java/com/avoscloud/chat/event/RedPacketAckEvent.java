package com.avoscloud.chat.event;

import com.avoscloud.chat.model.LCIMRedPcketAckMessage;

/**
 * Created by wli on 16/7/14.
 */
public class RedPacketAckEvent {
  public LCIMRedPcketAckMessage ackMessage;

  public RedPacketAckEvent(LCIMRedPcketAckMessage ackMessage) {
    this.ackMessage = ackMessage;
  }
}
