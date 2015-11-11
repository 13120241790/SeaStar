package com.rupeng.bean.response;

import com.rupeng.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class User extends BaseBean {

    private static final long serialVersionUID = -6111860036842114327L;

    /**
     * id : 80892
     * username : 陌路相逢
     * portrait : http://www.gravatar.com/avatar/4bbdad640e506e9990de6d640d2e379b?s=82&d=wavatar
     * token : nsxWsXQG8lJfiVhKxcKFKrI6ZiT8q7s0UEaMPWY0lMwKYBrSBc9k5CAuD9f5CJsPZAY7/7AjnATcTubv2Hl0eA==
     */

    private String id;
    private String username;
    private String portrait;
    private String token;

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getToken() {
        return token;
    }
}
