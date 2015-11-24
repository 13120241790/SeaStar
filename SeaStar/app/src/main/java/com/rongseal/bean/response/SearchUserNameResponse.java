package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

import java.util.List;

/**
 * Created by AMing on 15/11/13.
 * Company RongCloud
 */
public class SearchUserNameResponse  extends BaseBean{


    private static final long serialVersionUID = 4747861660239910098L;
    /**
     * code : 200
     * result : [{"id":"26595","username":"yang11","portrait":"http://www.gravatar.com/avatar/9b3b52b14f4ac5b4561f9d6a72153fb1?s=82&d=wavatar"},{"id":"22829","username":"Yang111","portrait":"http://www.gravatar.com/avatar/c74283bc89495414852a97c519dfc18a?s=82&d=wavatar"}]
     */

    private int code;
    /**
     * id : 26595
     * username : yang11
     * portrait : http://www.gravatar.com/avatar/9b3b52b14f4ac5b4561f9d6a72153fb1?s=82&d=wavatar
     */

    private List<ResultEntity> result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public List<ResultEntity> getResult() {
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
