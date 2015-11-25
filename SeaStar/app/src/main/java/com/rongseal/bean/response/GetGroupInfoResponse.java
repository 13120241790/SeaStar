package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

import java.util.List;

/**
 * Created by AMing on 15/11/25.
 * Company RongCloud
 */
public class GetGroupInfoResponse extends BaseBean{

    private static final long serialVersionUID = -7336994384400722252L;


    /**
     * code : 200
     * result : {"id":"21","name":"Android I 群","portrait":null,"introduce":"Android I 群加人了","number":"500","max_number":"500","create_user_id":"22775","creat_datetime":"2015-04-20 21:11:20","users":[{"id":"22775","username":"test111","portrait":"http://www.gravatar.com/avatar/1ade9e908a679f0d78808b3e50414db8?s=82&d=wavatar"},{"id":"35713","username":"zyj","portrait":"http://www.gravatar.com/avatar/fa6584123ef4c2934c236ff334564c39?s=82&d=wavatar"}]}
     */

    private int code;
    /**
     * id : 21
     * name : Android I 群
     * portrait : null
     * introduce : Android I 群加人了
     * number : 500
     * max_number : 500
     * create_user_id : 22775
     * creat_datetime : 2015-04-20 21:11:20
     * users : [{"id":"22775","username":"test111","portrait":"http://www.gravatar.com/avatar/1ade9e908a679f0d78808b3e50414db8?s=82&d=wavatar"},{"id":"35713","username":"zyj","portrait":"http://www.gravatar.com/avatar/fa6584123ef4c2934c236ff334564c39?s=82&d=wavatar"}]
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
        private String name;
        private Object portrait;
        private String introduce;
        private String number;
        private String max_number;
        private String create_user_id;
        private String creat_datetime;
        /**
         * id : 22775
         * username : test111
         * portrait : http://www.gravatar.com/avatar/1ade9e908a679f0d78808b3e50414db8?s=82&d=wavatar
         */

        private List<UsersEntity> users;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPortrait(Object portrait) {
            this.portrait = portrait;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setMax_number(String max_number) {
            this.max_number = max_number;
        }

        public void setCreate_user_id(String create_user_id) {
            this.create_user_id = create_user_id;
        }

        public void setCreat_datetime(String creat_datetime) {
            this.creat_datetime = creat_datetime;
        }

        public void setUsers(List<UsersEntity> users) {
            this.users = users;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Object getPortrait() {
            return portrait;
        }

        public String getIntroduce() {
            return introduce;
        }

        public String getNumber() {
            return number;
        }

        public String getMax_number() {
            return max_number;
        }

        public String getCreate_user_id() {
            return create_user_id;
        }

        public String getCreat_datetime() {
            return creat_datetime;
        }

        public List<UsersEntity> getUsers() {
            return users;
        }

        public static class UsersEntity {
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
}
