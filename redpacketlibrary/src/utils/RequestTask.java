package utils;

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
import com.yunzhanghu.redpacketsdk.RPCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;

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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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
                RedPacket.getInstance().initRPAuthToken(partner, userId, timestamp, sign,
                        new RPCallback() {
                            @Override
                            public void onSuccess() {
                                RedPacketUtils.getInstance().initAuthData(partner, userId, timestamp, sign);
                                // 进入主页面
                                Log.e(TAG, "init Red Packet success token: " + RedPacket.getInstance().sToken);
                                mHandler.obtainMessage(HANDLER_LOGIN_SUCCESS).sendToTarget();
                            }

                            @Override
                            public void onError(String code, String message) {
                                //错误处理
                                Log.e(TAG, "init Red Packet fail token:" + message);
                                mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
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
