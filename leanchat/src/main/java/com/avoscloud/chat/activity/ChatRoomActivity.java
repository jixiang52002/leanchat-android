package com.avoscloud.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avoscloud.chat.R;
import com.avoscloud.chat.util.ConversationUtils;

import cn.leanclud.imkit.activity.LCIMConversationActivity;
import cn.leanclud.imkit.event.LCIMLocationItemClickEvent;
import cn.leanclud.imkit.utils.LCIMConstants;

/**
 * Created by lzw on 15/4/24.
 */
public class ChatRoomActivity extends LCIMConversationActivity {
  public static final int LOCATION_REQUEST = 100;
  public static final int QUIT_GROUP_REQUEST = 200;
  private AVIMConversation conversation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
//    NotificationUtils.cancelNotification(this);
    super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.chat_ativity_menu, menu);
    if (null != menu && menu.size() > 0) {
      MenuItem item = menu.getItem(0);
      item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
        | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void updateConversation(AVIMConversation conversation) {
    super.updateConversation(conversation);
    this.conversation = conversation;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int menuId = item.getItemId();
    if (menuId == R.id.people) {
      if (null != conversation) {
        Intent intent = new Intent(ChatRoomActivity.this, ConversationDetailActivity.class);
        intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
        startActivityForResult(intent, QUIT_GROUP_REQUEST);
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case LOCATION_REQUEST:
          final double latitude = intent.getDoubleExtra(LocationActivity.LATITUDE, 0);
          final double longitude = intent.getDoubleExtra(LocationActivity.LONGITUDE, 0);
          final String address = intent.getStringExtra(LocationActivity.ADDRESS);
          if (!TextUtils.isEmpty(address)) {
            AVIMLocationMessage locationMsg = new AVIMLocationMessage();
            locationMsg.setLocation(new AVGeoPoint(latitude, longitude));
            locationMsg.setText(address);
            conversationFragment.sendMessage(locationMsg);
          } else {
            Toast.makeText(this, R.string.chat_cannotGetYourAddressInfo, Toast.LENGTH_SHORT).show();
          }
          break;
        case QUIT_GROUP_REQUEST:
          finish();
          break;
      }
    }
  }

  @Override
  protected void getConversation(String memberId) {
    super.getConversation(memberId);
    ConversationUtils.createSingleConversation(memberId, new AVIMConversationCreatedCallback() {
      @Override
      public void done(AVIMConversation avimConversation, AVIMException e) {
        updateConversation(avimConversation);
      }
    });
  }

//  public void onEvent(InputBottomBarLocationClickEvent event) {
//    LocationActivity.startToSelectLocationForResult(this, LOCATION_REQUEST);
//  }
//
  public void onEvent(LCIMLocationItemClickEvent event) {
    if (null != event && null != event.message && event.message instanceof AVIMLocationMessage) {
      AVIMLocationMessage locationMessage = (AVIMLocationMessage) event.message;
      LocationActivity.startToSeeLocationDetail(this, locationMessage.getLocation().getLatitude(),
        locationMessage.getLocation().getLongitude());
    }
  }
}
