package com.rongseal.bean.basebean;

import android.text.TextUtils;

import com.rongseal.common.Constants;
import com.rongseal.widget.pulltorefresh.PullToRefreshAdapterViewBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase.Mode;

import android.os.Handler;


/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class BaseResponse extends BaseBean {

    private static final long serialVersionUID = -7411682698542566208L;


    private String errorCode;
    private String errorMessage;
    private int countNum;
    private int currentPage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getCountNum() {
        return countNum;
    }

    public void setCountNum(int countNum) {
        this.countNum = countNum;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    /**
     * 判断是否成功
     * @return
     */
    public boolean isSuccess(){
        if(!TextUtils.isEmpty(errorCode) && errorCode.equals("0")){
            return true;
        }
        return false;
    }

    /**
     * 处理分页
     * @param refreshlistview
     * @param pageNo
     * @param totalCount
     */
    @SuppressWarnings("rawtypes")
    public void doPageInfo(final PullToRefreshAdapterViewBase refreshlistview, int pageNo, int totalCount){
        if((pageNo * Constants.PAGESIZE) >= totalCount){
            new Handler().postAtTime(new Runnable() {
                @Override
                public void run() {
                    refreshlistview.setMode(Mode.PULL_FROM_START);
                }
            }, 1000);
        }else{
            refreshlistview.setMode(Mode.BOTH);
        }
    }
}
