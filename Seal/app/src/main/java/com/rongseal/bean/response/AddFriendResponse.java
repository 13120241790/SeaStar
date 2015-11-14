package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/14.
 * Company RongCloud
 */
public class AddFriendResponse extends BaseBean{

    private static final long serialVersionUID = -3143368833474291733L;
    /**
     * code : 304
     * message : unknow error
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
