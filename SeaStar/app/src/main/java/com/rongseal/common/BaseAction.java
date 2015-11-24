package com.rongseal.common;

import android.content.Context;

import com.sd.core.common.parse.JsonMananger;
import com.sd.core.network.http.HttpException;
import com.sd.core.network.http.RequestParams;
import com.sd.core.network.http.SyncHttpClient;
import com.sd.core.utils.encrypt.BackAES;
import com.sd.core.utils.encrypt.MD5;

import java.util.List;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public abstract class BaseAction {

    protected Context mContext;
    protected SyncHttpClient httpManager;

    protected int pageSize = 20;

    /** 缓存有效期5分钟 **/
    protected final long INVALID_TIME = 5*60;
    /** 缓存有效期30分钟 **/
    protected final long INVALID_TIME_30MIN = 30*60;
    /** 缓存有效期1小时 **/
    protected final long INVALID_TIME_1HOURS = 60*60;
    /** 缓存有效期1天 **/
    protected final long INVALID_TIME_1DAY = 24*60*60;
    /** 缓存有效期1周 **/
    protected final long INVALID_TIME_1WEEK = 7*24*60*60;
    /** 缓存有效期1个月 **/
    protected final long INVALID_TIME_1MONTH = 30*24*60*60;
    /** key **/
    private static final String skey = "dAA%DdG*262r4I!V";

    /**
     * 构造方法
     * @param context
     */
    public BaseAction(Context context) {
        this.mContext = context;
        this.httpManager = SyncHttpClient.getInstance(context);
    }

    /**
     * JSON转JAVA对象方法
     * @param json
     * @param cls
     * @return
     * @throws HttpException
     */
    public <T> T jsonToBean(String json, Class<T> cls) throws HttpException {
        return JsonMananger.jsonToBean(json, cls);
    }

    /**
     * JSON转JAVA数组方法
     * @param json
     * @param
     * @return
     * @throws HttpException
     */
    public <T> List<T> jsonToList(String json, Class<T> cls) throws HttpException {
        return JsonMananger.jsonToList(json, cls);
    }

    /**
     * JAVA对象转JSON方法
     * @param obj
     * @return
     * @throws HttpException
     */
    public String BeanTojson(Object obj) throws HttpException {
        return JsonMananger.beanToJson(obj);
    }

    /**
     * 获取公共的参数
     * @return
     */
    public RequestParams getRequestParams(){
        RequestParams params = new RequestParams();
        //1web端,2Ios手机,3安卓手机,4ios平板,5安卓平板,6微信
        params.put("channelType", "5");
        return params;
    }

    /**
     * 获取公共的参数
     * @return
     * @throws HttpException
     */
    public RequestParams getSignParams(RequestParams params) throws HttpException{
        params.put("signature", MD5.encrypt(params.getParamString()));
        RequestParams signParams = new RequestParams();
        String content = BeanTojson(params.getUrlParams());
        content = new String(BackAES.encrypt(content, skey, 0));
        signParams.put("content", content);

        return signParams;
    }

    /**
     * 获取完整URL方法
     * @param url
     * @return
     */
    protected String getURL(String url) {
        return getURL(url, new String[]{});
    }

    /**
     * 获取完整URL方法
     * @param url
     * @param params
     * @return
     */
    protected String getURL(String url, String... params) {
        StringBuilder urlBilder = new StringBuilder(Constants.DOMAIN).append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBilder.toString().endsWith("/")) {
                    urlBilder.append("/");
                }
                urlBilder.append(param);
            }
        }
        return urlBilder.toString();
    }

    /**
     * 获取缓存key
     * @param url
     * @param params
     * @return
     */
    protected String getCacheKey(String url, RequestParams params){
        StringBuilder key = new StringBuilder(url);
        if(params != null){
            key.append(params.getParamString());
        }
        return String.valueOf(key.toString().hashCode());
    }
}
