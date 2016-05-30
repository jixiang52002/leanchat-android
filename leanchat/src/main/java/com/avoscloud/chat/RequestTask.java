package com.avoscloud.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RedPacket;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class RequestTask extends AsyncTask<String, String, String> {
     private final String TAG="";
     private String userID;
     private Context context;
     private final int HANDLER_LOGIN_SUCCESS=1;
     private final int HANDLER_LOGIN_FAILURE=0;
     @SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
 			super.handleMessage(msg);
 			switch(msg.what){
 			case HANDLER_LOGIN_SUCCESS:
 				Toast.makeText(context, "红包SDK登陆成功", Toast.LENGTH_SHORT).show();
 				
 				break;
 			case HANDLER_LOGIN_FAILURE:
 				Toast.makeText(context, "红包SDK登陆失败", Toast.LENGTH_SHORT).show();
 				break;
 			}
		}
    	 
    	 
     };
     public RequestTask(Context context, String userID){
    	 
    	 this.context=context;
    	 this.userID=userID;
    	 
     }
    @Override
    protected String doInBackground(String... uri) {
         Log.e(TAG, "—UserId—" + userID);
        String mockUrl = "http://rpv2.yunzhanghu.com/api/sign?duid=" + userID;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(mockUrl);
        // replace with your url
        HttpResponse response;
        try {
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                Log.d("Response of GET request", response.toString());
                String responseBody = EntityUtils.toString(response.getEntity());
                return responseBody;
            }

            return null;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage());
        }
        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result != null) {
                Log.e("zyh", "-res-" + result);
                JSONObject jsonObj = new JSONObject(result);
                String partner = jsonObj.getString("partner");
                String userId = jsonObj.getString("user_id");
                String timestamp = jsonObj.getString("timestamp");
                String sign = jsonObj.getString("sign");
                //  String regHongbaoUser = jsonObj.getString("reg_hongbao_user");
                // {"partner":"246606","user_id":"130374","timestamp":1464087428,"reg_hongbao_user":1,"sign":"3ba823465be1552ff7c4723a6d88fe26cfe3ed87a6949076e34f7ddb3dd9d5a3"}
                RedPacket.getInstance().initRPAuthToken(partner, userId, timestamp, sign,
                        new RPCallback() {
                            @Override
                            public void onSuccess() {
                                // 进入主页面
                                mHandler.obtainMessage(HANDLER_LOGIN_SUCCESS).sendToTarget();
                                Log.e(TAG, "init luck money success token: " + RedPacket.getInstance().sToken);
                            }

                            @Override
                            public void onError(String code, String message) {
                                //错误处理
                                mHandler.obtainMessage(HANDLER_LOGIN_FAILURE).sendToTarget();
                                Log.e(TAG, "init luck money fail token:" + message);

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
