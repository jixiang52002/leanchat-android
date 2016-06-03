package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;


import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;

import java.util.Map;

import utils.RedPacketUtils;

/**
 * Created by ustc on 2016/6/2.
 */
public class HideRedPacketChatItemHolder  extends CommonViewHolder {

    protected TextView contentView;
    View view;
    public HideRedPacketChatItemHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.rp_chat_item_hide);

    }


    @Override
    public void bindData(Object o) {

    }
}
