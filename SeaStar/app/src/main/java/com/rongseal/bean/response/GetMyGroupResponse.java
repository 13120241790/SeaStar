package com.rongseal.bean.response;

import com.rongseal.bean.basebean.BaseBean;

import java.util.List;

/**
 * Created by AMing on 15/11/24.
 * Company RongCloud
 */
public class GetMyGroupResponse extends BaseBean{

    private static final long serialVersionUID = -8209532150474133637L;


    /**
     * code : 200
     * result : [{"id":"25","name":"iOS III 群","join_date":"2015-07-04 18:38:19"},{"id":"21","name":"Android I 群","join_date":"2015-08-06 11:45:09"}]
     */

    private int code;
    /**
     * id : 25
     * name : iOS III 群
     * join_date : 2015-07-04 18:38:19
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
        private String join_date;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getJoin_date() {
            return join_date;
        }
    }
}
