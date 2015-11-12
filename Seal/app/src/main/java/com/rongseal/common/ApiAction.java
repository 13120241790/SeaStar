package com.rongseal.common;

import android.content.Context;
import android.text.TextUtils;

import com.rongseal.bean.response.LoginResponse;
import com.rongseal.bean.response.RegistResponse;
import com.sd.core.network.http.HttpException;
import com.sd.core.network.http.RequestParams;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class ApiAction extends BaseAction {
    /**
     * 构造方法
     *
     * @param context
     */
    public ApiAction(Context context) {
        super(context);
    }

    public RegistResponse regist(String email , String password , String username ,String moblie) throws HttpException{
        String url = getURL("reg");
        RequestParams params = getRequestParams();
        params.put("email",email);
        params.put("password", password);
        params.put("username",username);
        params.put("moblie",moblie);
        RegistResponse response = null;
        String result = httpManager.post(mContext,url,params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result,RegistResponse.class);
        }
        return response;
    }


    public LoginResponse login(String email , String password) throws HttpException{
        String uri = getURL("email_login_token");
       RequestParams params =  getRequestParams();
        params.put("email",email);
        params.put("password",password);
        LoginResponse response = null;
        String result = httpManager.post(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result,LoginResponse.class);
        }
        return response;
    }
}
