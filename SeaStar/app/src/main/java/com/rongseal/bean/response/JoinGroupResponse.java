package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/23.
 * Company RongCloud
 */
public class JoinGroupResponse extends BaseBean{
    private static final long serialVersionUID = -3745010150115404072L;


    /**
     * code : 110
     * message : user not login
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
