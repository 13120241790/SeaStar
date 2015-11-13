package com.rongseal.common;

import android.content.Context;
import android.text.TextUtils;

import com.rongseal.bean.response.LoginResponse;
import com.rongseal.bean.response.RegistResponse;
import com.rongseal.bean.response.SearchEmailResponse;
import com.rongseal.bean.response.SearchUserNameResponse;
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

    /**
     * 注册
     * @param email
     * @param password
     * @param username
     * @param moblie
     * @return
     * @throws HttpException
     */
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

    /**
     * 登录
     * @param email
     * @param password
     * @return
     * @throws HttpException
     */
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

    /**
     * 搜索昵称查找用户
     * @param username
     * @return
     * @throws HttpException
     */
    public SearchUserNameResponse searchUserName(String username) throws HttpException{
        String uri = getURL("seach_name");
        RequestParams params =  getRequestParams();
        params.put("username",username);
        SearchUserNameResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result,SearchUserNameResponse.class);
        }
        return response;
    }

    /**
     * 搜索邮箱查找用户
     * @param email
     * @return
     * @throws HttpException
     */
    public SearchEmailResponse searchEmail(String email) throws HttpException{
        String uri = getURL("seach_email");
        RequestParams params =  getRequestParams();
        params.put("email",email);
        SearchEmailResponse response = null;
        String result = httpManager.get(uri,params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result,SearchEmailResponse.class);
        }
        return response;
    }
}
