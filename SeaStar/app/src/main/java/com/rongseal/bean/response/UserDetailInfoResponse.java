package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

/**
 * Created by AMing on 15/11/26.
 * Company RongCloud
 */
public class UserDetailInfoResponse extends BaseBean{

    private static final long serialVersionUID = 7114233020637560923L;


    /**
     * code : 200
     * result : {"id":"22830","username":"Yang112","portrait":"http://www.gravatar.com/avatar/4f8a6149e283c598f5c772715c117747?s=82&d=wavatar"}
     */

    private int code;
    /**
     * id : 22830
     * username : Yang112
     * portrait : http://www.gravatar.com/avatar/4f8a6149e283c598f5c772715c117747?s=82&d=wavatar
     */

    private ResultEntity result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        private String id;
        private String username;
        private String portrait;

        public void setId(String id) {
            this.id = id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
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
    }
}
