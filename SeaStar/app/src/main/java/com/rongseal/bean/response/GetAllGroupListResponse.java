package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

import java.util.List;

/**
 * Created by AMing on 15/11/23.
 * Company RongCloud
 */
public class GetAllGroupListResponse extends BaseBean {
    private static final long serialVersionUID = -146606778506052431L;


    /**
     * code : 200
     * result : [{"id":"20","name":"大融云","portrait":null,"introduce":"大融云升级了...","number":"500","max_number":"500","create_user_id":"22775","creat_datetime":"2015-04-20 21:09:35"},{"id":"21","name":"Android I 群","portrait":null,"introduce":"Android I 群加人了","number":"500","max_number":"500","create_user_id":"22775","creat_datetime":"2015-04-20 21:11:20"}]
     */

    private int code;
    /**
     * id : 20
     * name : 大融云
     * portrait : null
     * introduce : 大融云升级了...
     * number : 500
     * max_number : 500
     * create_user_id : 22775
     * creat_datetime : 2015-04-20 21:09:35
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
        private String name;
        private String portrait;
        private String introduce;
        private String number;
        private String max_number;
        private String create_user_id;
        private String creat_datetime;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }


        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getIntroduce() {
            return introduce;
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

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
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
    }
}
