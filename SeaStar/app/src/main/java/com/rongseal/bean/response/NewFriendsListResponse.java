package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

import java.util.List;

/**
 * Created by AMing on 15/11/17.
 * Company RongCloud
 */
public class NewFriendsListResponse extends BaseBean{

    private static final long serialVersionUID = -2469912986824746634L;


    /**
     * code : 200
     * result : [{"id":"6754","email":"yangpan@feinno.com","username":"Ariel@iPhone","portrait":"http://www.gravatar.com/avatar/3f56d1043edd4b9657c465ac7a507067?s=82","status":"1"},{"id":"19237","email":"18919026893@163.com","username":"hahajaj","portrait":"http://www.gravatar.com/avatar/6405153265d25fd4b6dccfacf0fe410c?s=82","status":"1"}]
     */

    private int code;
    /**
     * id : 6754
     * email : yangpan@feinno.com
     * username : Ariel@iPhone
     * portrait : http://www.gravatar.com/avatar/3f56d1043edd4b9657c465ac7a507067?s=82
     * status : 1
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
        private String email;
        private String username;
        private String portrait;
        private String status;

        public void setId(String id) {
            this.id = id;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPortrait() {
            return portrait;
        }

        public String getStatus() {
            return status;
        }
    }
}
