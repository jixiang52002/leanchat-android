package com.avoscloud.chat.redpacket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RequestTask {
  private final String TAG = "LeanCloud_Login";
  private final int HANDLER_LOGIN_SUCCESS = 1;
  private final int HANDLER_LOGIN_FAILURE = 0;
  private RequestQueue mQueue;
  private static RequestTask mRequestTask;
  @SuppressLint("HandlerLeak")
  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case HANDLER_LOGIN_SUCCESS:
          Log.e("msg", "----->红包SDK登录成功");
          break;
        case HANDLER_LOGIN_FAILURE:
          Log.e("msg", "----->红包SDK登录失败");
          break;
      }
    }
  };

  public static RequestTask getInstance() {
    if (mRequestTask == null) {
      synchronized (RedPacketUtils.class) {
        if (mRequestTask == null) {
          mRequestTask = new RequestTask();
        }
      }
    }
    return mRequestTask;
  }

  public void initRedPacketNet(Context context, String userID) {

    String mockUrl = "http://rpv2.yunzhanghu.com/api/sign?duid=" + userID;
    mQueue = Volley.newRequestQueue(context);
    StringRequest stringRequest = new StringRequest(mockUrl, new Response.Listener<String>() {
      @Override
      public void onResponse(String s) {
        ParseJson(s);
        //进入主页面
        mHandler.obtainMessage(HANDLER_LOGIN_SUCCESS).sendToTarget();
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError volleyError) {
        //错误处理
        mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
      }
    });
    stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
    mQueue.add(stringRequest);
  }

  /**
   * 解析数据
   *
   * @param result
   */
  private void ParseJson(String result) {
    try {
      if (result != null) {
        JSONObject jsonObj = new JSONObject(result);
        final String partner = jsonObj.getString("partner");
        final String userId = jsonObj.getString("user_id");
        final String timestamp = jsonObj.getString("timestamp");
        final String sign = jsonObj.getString("sign");
        /**
         * 零钱页和领取红包和发红包时都需要
         */
        RedPacketUtils.getInstance().initTokenData(partner, userId, timestamp, sign);
      } else {
        mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
