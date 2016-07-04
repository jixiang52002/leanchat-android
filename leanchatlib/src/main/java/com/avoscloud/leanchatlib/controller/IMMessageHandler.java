package com.avoscloud.leanchatlib.controller;

import android.content.Context;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avoscloud.leanchatlib.event.ImMessageEvent;
import com.avoscloud.leanchatlib.utils.LogUtils;

import de.greenrobot.event.EventBus;


/**
 * Created by ustc on 2016/6/14.
 */
public class IMMessageHandler extends AVIMMessageHandler {


    private Context context;

    public IMMessageHandler(Context context) {
        this.context = context.getApplicationContext();
    }


    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {

        if (message == null || message.getMessageId() == null) {
            LogUtils.d("may be SDK Bug, message or message id is null");
            return;
        }
        if (!ConversationHelper.isValidConversation(conversation)) {
            LogUtils.d("receive msg from invalid conversation");
        }

        if (ChatManager.getInstance().getSelfId() == null) {
            LogUtils.d("selfId is null, please call setupManagerWithUserId ");
            client.close(null);
        } else {
            if (!client.getClientId().equals(ChatManager.getInstance().getSelfId())) {
                client.close(null);
            } else {
                ChatManager.getInstance().getRoomsTable().insertRoom(message.getConversationId());
                if (!message.getFrom().equals(client.getClientId())) {
                    ChatManager.getInstance().getRoomsTable().increaseUnreadCount(message.getConversationId());
                    sendEvent(message, conversation);
                }
            }
        }
    }

    @Override
    public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessageReceipt(message, conversation, client);
    }

    /**
     * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理
     * 稍后应该加入 db
     *
     * @param message
     * @param conversation
     */
    private void sendEvent(AVIMMessage message, AVIMConversation conversation) {
        ImMessageEvent event = new ImMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

}
