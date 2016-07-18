package com.avoscloud.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.okhttp.Call;
import com.avos.avoscloud.okhttp.OkHttpClient;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.Response;
import com.avoscloud.chat.util.LogUtils;
import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RedPacket;

import org.json.JSONObject;

import java.io.IOException;

public class RequestTask extends AsyncTask<String, String, String> {
    private final String TAG = "";
    private String userID;
    private Context context;
    private final int HANDLER_LOGIN_SUCCESS = 1;
    private final int HANDLER_LOGIN_FAILURE = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_LOGIN_SUCCESS:
                    LogUtils.e("msg", "----->红包SDK登录成功");
                    break;
                case HANDLER_LOGIN_FAILURE:
                    LogUtils.e("msg", "----->红包SDK登录失败");
                    break;
            }
        }
    };

    public RequestTask(Context context, String userID) {
        this.context = context;
        this.userID = userID;
    }

    @Override
    protected String doInBackground(String... uri) {
        String mockUrl = "http://rpv2.yunzhanghu.com/api/sign?duid=" + userID;

      OkHttpClient client = new OkHttpClient();
      Request.Builder builder = new Request.Builder();
      builder.url(mockUrl).get();
      Call call = client.newCall(builder.build());
      try {
        Response response = call.execute();

        if (response.code() == 200) {
          Log.d("Response of GET request", response.toString());
          return new String(response.body().bytes(), "UTF-8");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result != null) {
                JSONObject jsonObj = new JSONObject(result);
                String partner = jsonObj.getString("partner");
                String userId = jsonObj.getString("user_id");
                String timestamp = jsonObj.getString("timestamp");
                String sign = jsonObj.getString("sign");
                RedPacket.getInstance().initRPAuthToken(partner, userId, timestamp, sign,
                        new RPCallback() {
                            @Override
                            public void onSuccess() {
                                // 进入主页面
                                mHandler.obtainMessage(HANDLER_LOGIN_SUCCESS).sendToTarget();
                                Log.e(TAG, "init Red Packet success token: " + RedPacket.getInstance().sToken);
                            }

                            @Override
                            public void onError(String code, String message) {
                                //错误处理
                                mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
                                Log.e(TAG, "init Red Packet fail token:" + message);
                            }
                        });
            } else {
                mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
