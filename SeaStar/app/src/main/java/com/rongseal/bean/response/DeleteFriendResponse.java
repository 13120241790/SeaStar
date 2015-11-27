package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/27.
 * Company RongCloud
 */
public class DeleteFriendResponse extends BaseBean{

    private static final long serialVersionUID = -4608421983587594004L;


    /**
     * code : 111
     * message : credential is error
     */

    private int code;
    private String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
