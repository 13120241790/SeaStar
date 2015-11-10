package com.rupeng.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rupeng.bean.response.RegistResponse;
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
        Log.e("regist",result);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result,RegistResponse.class);
        }
        return response;
    }
}
