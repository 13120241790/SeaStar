package com.rongseal.bean;

import com.rongseal.bean.basebean.BaseBean;


/**
 * Created by AMing on 15/11/3.
 * Company RongCloud
 */
public class Friend extends BaseBean{

    String userId;

    String name;

    String uri; // 头像 uri

    String letters; // 字母

    String token; // 融云 token

    //这个构造临时模拟数据使用
    public Friend(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public Friend() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
