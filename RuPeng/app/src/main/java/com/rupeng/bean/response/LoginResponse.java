package com.rupeng.bean.response;

import com.rupeng.bean.basebean.BaseBean;


/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class LoginResponse extends BaseBean{

    private static final long serialVersionUID = -2576356795768630090L;


    /**
     * code : 200
     * result : {"id":"80892","username":"陌路相逢","portrait":"http://www.gravatar.com/avatar/4bbdad640e506e9990de6d640d2e379b?s=82&d=wavatar","token":"nsxWsXQG8lJfiVhKxcKFKrI6ZiT8q7s0UEaMPWY0lMwKYBrSBc9k5CAuD9f5CJsPZAY7/7AjnATcTubv2Hl0eA=="}
     */

    private int code;
    /**
     * id : 80892
     * username : 陌路相逢
     * portrait : http://www.gravatar.com/avatar/4bbdad640e506e9990de6d640d2e379b?s=82&d=wavatar
     * token : nsxWsXQG8lJfiVhKxcKFKrI6ZiT8q7s0UEaMPWY0lMwKYBrSBc9k5CAuD9f5CJsPZAY7/7AjnATcTubv2Hl0eA==
     */

    private User result;

    public void setCode(int code) {
        this.code = code;
    }

    public User getResult() {
        return result;
    }

    public void setResult(User result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }


}
