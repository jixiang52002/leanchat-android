package com.avoscloud.leanchatlib.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.EmotionHelper;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lzw on 15/4/27.
 */
public class Utils {
    public static String millisecsToDateString(long timestamp) {
        long gap = System.currentTimeMillis() - timestamp;
        if (gap < 1000 * 60 * 60 * 24) {
            String s = (new PrettyTime()).format(new Date(timestamp));
            return s;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            return format.format(new Date(timestamp));
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
        }
    }
    static  CharSequence cTemp="";
    public static CharSequence getMessageeShorthand(final Context context, AVIMMessage message) {
        if (message instanceof AVIMTypedMessage) {
            AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(((AVIMTypedMessage) message).getMessageType());
            switch (type) {
                case TextMessageType:
                    Map<String, Object> attrs = ((AVIMTextMessage) message).getAttrs();
                    if (attrs != null && attrs.containsKey("redpacket")) {
                        JSONObject rpJSON = (JSONObject) attrs.get("redpacket");
                        if (rpJSON != null && rpJSON.size() != 0) {
                            String money_greeting = rpJSON.getString("money_greeting");
                            return "[LeanCloud红包]" + money_greeting;
                        }
                    }
                    return EmotionHelper.replace(context, ((AVIMTextMessage) message).getText());
                case ImageMessageType:
                    return "[图片]";
                case LocationMessageType:
                    return "[位置]";
                case AudioMessageType:
                    return "[语音]";
                default:
                    return "[未知]";
            }
        } else {
            try {
                JSONObject jsonObject = JSONObject.parseObject(message.getContent());
                if (jsonObject != null) if (jsonObject.containsKey("redpacket")) {
                    ChatManager chatManager = ChatManager.getInstance();
                    String selfId = chatManager.getSelfId();
                    if (jsonObject.containsKey("type") && jsonObject.getString("type").equals("redpacket_taken")) {
                        JSONObject rpJSON = jsonObject.getJSONObject("redpacket");
                        if (rpJSON.getString("money_sender_id").equals(selfId)) {

                            if (rpJSON.getString("money_receiver_id").equals(selfId)) {
                                String money_sender = rpJSON.getString("money_sender");
                                return "你领取了自己的红包";
                            }
                            String money_receiver = rpJSON.getString("money_receiver");
                            return money_receiver + "领取了你的红包";
                        } else if (rpJSON.getString("money_receiver_id").equals(selfId)) {
                            String money_sender = rpJSON.getString("money_sender");
                            return "你领取了" + money_sender + "的红包";
                        }else{

                            AVIMConversation conversation=   AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(message.getConversationId());
                            /**
                             * 拉取消息，必须加入 conversation 后才能拉取消息
                             */
                             conversation.queryMessages(new AVIMMessagesQueryCallback() {
                                    @Override
                                    public void done(List<AVIMMessage> list, AVIMException e) {
                                        if(filterException(e)){

                                            cTemp=      checkMsgs(list,context);
                                        }

                                    }
                                });
                              if(!TextUtils.isEmpty(cTemp)){

                                  return cTemp;
                              }

                        }
                    }
                }
            } catch (JSONException exception) {


            }
            return "[LeanCloud 红包]";
        }
    }
    private static boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            //    toast(e.getMessage());
            return false;
        } else return true;
    }

    private static CharSequence checkMsgs(List<AVIMMessage> list,Context context) {
        CharSequence temp = "";
        for (int i = 0; i <list.size(); i++) {
            AVIMMessage message = list.get(list.size()-i-1);

            System.out.println("list.size()-------------------->"+ list.size());
            System.out.println("i-------------------->"+i);
            System.out.println("message-------------------->"+message.getContent());
            if (message instanceof AVIMTypedMessage) {
                temp = isTypeMessage(context,message);
                break;
            } else {

                try {
                    JSONObject jsonObject = JSONObject.parseObject(message.getContent());
                    if (jsonObject != null) if (jsonObject.containsKey("redpacket")) {
                        ChatManager chatManager = ChatManager.getInstance();
                        String selfId = chatManager.getSelfId();
                        if (jsonObject.containsKey("type") && jsonObject.getString("type").equals("redpacket_taken")) {
                            JSONObject rpJSON = jsonObject.getJSONObject("redpacket");
                            if (rpJSON.getString("money_sender_id").equals(selfId)) {
                                if (rpJSON.getString("money_receiver_id").equals(selfId)) {
                                    String money_sender = rpJSON.getString("money_sender");
                                    temp = "你领取了自己的红包";
                                    break;

                                }
                                String money_receiver = rpJSON.getString("money_receiver");
                                temp = money_receiver + "领取了你的红包";
                                break;
                            } else if (rpJSON.getString("money_receiver_id").equals(selfId)) {
                                String money_sender = rpJSON.getString("money_sender");
                                temp = "你领取了" + money_sender + "的红包";
                                break;

                            }
                        }
                    }
                } catch (JSONException exception) {


                }


            }

        }

        return temp;
    }


    private static CharSequence isTypeMessage(Context context,AVIMMessage message) {

        AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(((AVIMTypedMessage) message).getMessageType());
        switch (type) {
            case TextMessageType:
                Map<String, Object> attrs = ((AVIMTextMessage) message).getAttrs();
                if (attrs != null && attrs.containsKey("redpacket")) {
                    JSONObject rpJSON = (JSONObject) attrs.get("redpacket");
                    if (rpJSON != null && rpJSON.size() != 0) {
                        String money_greeting = rpJSON.getString("money_greeting");
                        return "[LeanCloud红包]" + money_greeting;
                    }
                }
                return EmotionHelper.replace(context, ((AVIMTextMessage) message).getText());
            case ImageMessageType:
                return "[图片]";
            case LocationMessageType:
                return "[位置]";
            case AudioMessageType:
                return "[语音]";
            default:
                return "[未知]";
        }
    }
}