package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/21.
 * Company RongCloud
 */
public class UpdateUserNameResponse extends BaseBean{

    private static final long serialVersionUID = 5666346868808716213L;
    /**
     * code : 200
     */

    private int code;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
