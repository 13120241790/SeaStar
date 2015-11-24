package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/18.
 * Company RongCloud
 */
public class FeedBackFriendRequestResponse extends BaseBean{

    private static final long serialVersionUID = 892804456748844442L;


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
