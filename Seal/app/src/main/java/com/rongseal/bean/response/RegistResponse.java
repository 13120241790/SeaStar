package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseResponse;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class RegistResponse extends BaseResponse {


    private static final long serialVersionUID = 412163523810220354L;


    /**
     * 返回码
     */
    private int code;

    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
