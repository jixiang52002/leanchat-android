package com.avoscloud.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.leanchatlib.activity.AVBaseActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.redpacket.RedPacketUtils;
import com.avoscloud.leanchatlib.redpacket.RequestTask;

public class EntrySplashActivity extends AVBaseActivity {
    public static final int SPLASH_DURATION = 2000;
    private static final int GO_MAIN_MSG = 1;
    private static final int GO_LOGIN_MSG = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAIN_MSG:
                    initUserData();
                    imLogin();
                    break;
                case GO_LOGIN_MSG:
                    Intent intent = new Intent(EntrySplashActivity.this, EntryLoginActivity.class);
                    EntrySplashActivity.this.startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_splash_layout);
        if (LeanchatUser.getCurrentUser() != null) {
            LeanchatUser.getCurrentUser().updateUserInfo();
            handler.sendEmptyMessageDelayed(GO_MAIN_MSG, SPLASH_DURATION);
        } else {
            handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
        }
    }

    private void imLogin() {
        ChatManager.getInstance().openClient(this, LeanchatUser.getCurrentUserId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (filterException(e)) {
                    RequestTask.getInstance().initRedPacketNet(getApplicationContext(), LeanchatUser.getCurrentUserId());
                    Intent intent = new Intent(EntrySplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 初始化登录用户数据
     */
    private void initUserData() {
        LeanchatUser curuser = LeanchatUser.getCurrentUser();
        if (!TextUtils.isEmpty(LeanchatUser.getCurrentUserId())) {
            RedPacketUtils.getInstance().setUserid(curuser.getCurrentUserId());
        } else {
            RedPacketUtils.getInstance().setUserid("");
        }
        if (curuser != null) {
            if (!TextUtils.isEmpty(curuser.getUsername())) {
                RedPacketUtils.getInstance().setUserName(curuser.getUsername());
            } else {
                RedPacketUtils.getInstance().setUserName("");
            }
            if (!TextUtils.isEmpty(curuser.getAvatarUrl())) {
                RedPacketUtils.getInstance().setUserAvatar(curuser.getAvatarUrl());
            } else {
                RedPacketUtils.getInstance().setUserAvatar("");
            }
        }
    }
}
