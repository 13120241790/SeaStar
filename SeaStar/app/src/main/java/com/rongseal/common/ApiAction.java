package com.rongseal.common;

import android.content.Context;
import android.text.TextUtils;

import com.rongseal.bean.response.AddFriendResponse;
import com.rongseal.bean.response.FeedBackFriendRequestResponse;
import com.rongseal.bean.response.GetAllGroupListResponse;
import com.rongseal.bean.response.GetGroupInfoResponse;
import com.rongseal.bean.response.GetMyGroupResponse;
import com.rongseal.bean.response.JoinGroupResponse;
import com.rongseal.bean.response.LoginResponse;
import com.rongseal.bean.response.NewFriendsListResponse;
import com.rongseal.bean.response.QuitGroupResponse;
import com.rongseal.bean.response.RegistResponse;
import com.rongseal.bean.response.SearchEmailResponse;
import com.rongseal.bean.response.SearchUserNameResponse;
import com.rongseal.bean.response.UpdateUserNameResponse;
import com.rongseal.bean.response.UserDetailInfoResponse;
import com.sd.core.network.http.HttpException;
import com.sd.core.network.http.RequestParams;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class ApiAction extends BaseAction {
    /**
     * 构造方法
     *
     * @param context
     */
    public ApiAction(Context context) {
        super(context);
    }

    /**
     * 注册
     *
     * @param email
     * @param password
     * @param username
     * @param moblie
     * @return
     * @throws HttpException
     */
    public RegistResponse regist(String email, String password, String username, String moblie) throws HttpException {
        String url = getURL("reg");
        RequestParams params = getRequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("username", username);
        params.put("moblie", moblie);
        RegistResponse response = null;
        String result = httpManager.post(mContext, url, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, RegistResponse.class);
        }
        return response;
    }

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     * @throws HttpException
     */
    public LoginResponse login(String email, String password) throws HttpException {
        String uri = getURL("email_login_token");
        RequestParams params = getRequestParams();
        params.put("email", email);
        params.put("password", password);
        LoginResponse response = null;
        String result = httpManager.post(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, LoginResponse.class);
        }
        return response;
    }

    /**
     * 搜索昵称查找用户
     *
     * @param username
     * @return
     * @throws HttpException
     */
    public SearchUserNameResponse searchUserName(String username) throws HttpException {
        String uri = getURL("seach_name");
        RequestParams params = getRequestParams();
        params.put("username", username);
        SearchUserNameResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SearchUserNameResponse.class);
        }
        return response;
    }

    /**
     * 搜索邮箱查找用户
     *
     * @param email
     * @return
     * @throws HttpException
     */
    public SearchEmailResponse searchEmail(String email) throws HttpException {
        String uri = getURL("seach_email");
        RequestParams params = getRequestParams();
        params.put("email", email);
        SearchEmailResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SearchEmailResponse.class);
        }
        return response;
    }

    /**
     * 请求加好友
     *
     * @param userid
     * @param message
     * @return
     * @throws HttpException
     */
    public AddFriendResponse addFriend(String userid, String message) throws HttpException {
        String uri = getURL("request_friend");
        RequestParams params = getRequestParams();
        params.put("id", userid);
        params.put("message", message);
        AddFriendResponse response = null;
        String result = httpManager.post(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AddFriendResponse.class);
        }
        return response;
    }

    /**
     * 获取新朋友的列表
     *
     * @return
     * @throws HttpException
     */
    public NewFriendsListResponse getNewFriendsList() throws HttpException {
        String uri = getURL("get_friend");
        NewFriendsListResponse response = null;
        String result = httpManager.get(uri);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, NewFriendsListResponse.class);
        }
        return response;
    }

    /**
     * 处理好友请求
     *
     * @param userid
     * @param isaccess 1为同意 不点击为 默认不处理
     * @return
     * @throws HttpException
     */
    public FeedBackFriendRequestResponse FeedBackFriendRequest(String userid, String isaccess) throws HttpException {
        String uri = getURL("process_request_friend");
        RequestParams params = getRequestParams();
        params.put("id", userid);
        params.put("is_access", isaccess);
        FeedBackFriendRequestResponse response = null;
        String result = httpManager.post(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, FeedBackFriendRequestResponse.class);
        }
        return response;
    }

    /**
     * 获取服务器上所有的群组
     *
     * @return
     * @throws HttpException
     */
    public GetAllGroupListResponse getAllGroupList() throws HttpException {
        String uri = getURL("get_all_group");
        GetAllGroupListResponse response = null;
        String result = httpManager.get(uri);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetAllGroupListResponse.class);
        }
        return response;
    }

    /**
     * 申请加入群组
     *
     * @param gruopId
     * @return
     * @throws HttpException
     */
    public JoinGroupResponse JoinGroup(String gruopId) throws HttpException {
        String uri = getURL("join_group");
        RequestParams params = getRequestParams();
        params.put("id", gruopId);
        JoinGroupResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, JoinGroupResponse.class);
        }
        return response;
    }

    /**
     * 获取我所在的群组
     *
     * @return
     * @throws HttpException
     */
    public GetMyGroupResponse getMyGruop() throws HttpException {
        String uri = getURL("get_my_group");
        GetMyGroupResponse response = null;
        String result = httpManager.get(uri);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetMyGroupResponse.class);
        }
        return response;
    }

    /**
     * 根据群组 id 获取群组信息
     *
     * @param groupId
     * @return
     * @throws HttpException
     */
    public GetGroupInfoResponse getGroupInfo(String groupId) throws HttpException {
        String uri = getURL("get_group");
        RequestParams params = getRequestParams();
        params.put("id", groupId);
        GetGroupInfoResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetGroupInfoResponse.class);
        }
        return response;
    }

    /**
     * 退出群组
     *
     * @param groupId
     * @return
     * @throws HttpException
     */
    public QuitGroupResponse quitGroup(String groupId) throws HttpException {
        String uri = getURL("quit_group");
        RequestParams params = getRequestParams();
        params.put("id", groupId);
        QuitGroupResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, QuitGroupResponse.class);
        }
        return response;
    }


    /**
     * 根据 userid 获取个人信息
     * @param userid
     * @return
     * @throws HttpException
     */
    public UserDetailInfoResponse getUserDetailInfo(String userid) throws HttpException {
        String uri = getURL("profile");
        RequestParams params = getRequestParams();
        params.put("id", userid);
        UserDetailInfoResponse response = null;
        String result = httpManager.get(uri, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, UserDetailInfoResponse.class);
        }
        return response;
    }

    public UpdateUserNameResponse UpdateUserName(String newName) {
        String url = getURL("");
        RequestParams params = getRequestParams();
        params.put("username", newName);
        return null;
    }
}
