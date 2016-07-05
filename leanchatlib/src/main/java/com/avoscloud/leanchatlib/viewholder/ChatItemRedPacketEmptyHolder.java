package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.avoscloud.leanchatlib.R;

/**
 * Created by ust on 2016/6/2.
 */
public class ChatItemRedPacketEmptyHolder extends CommonViewHolder {

    View view;

    public ChatItemRedPacketEmptyHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.lc_chat_item_empty);
    }

    @Override
    public void bindData(Object o) {
    }
}
