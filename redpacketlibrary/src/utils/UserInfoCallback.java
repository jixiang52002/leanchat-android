package utils;

import com.yunzhanghu.redpacketsdk.bean.RPUserBean;

import java.util.List;

/**
 * Created by hhx on 16/6/29.
 */
public interface UserInfoCallback {
    void getUserInfo(List<RPUserBean> userlist);
}
