package com.rongseal.message;

import android.os.Parcel;
import android.util.Log;

import com.sea_monster.common.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Bob on 2015/4/19.
 * 演示如何自定义消息
 */
@MessageTag(value = "RC:AgreeReq")
public class AgreedFriendRequestMessage extends MessageContent {

    private String friendId;
    private String message;

    public AgreedFriendRequestMessage(String friendId, String message) {
        this.friendId = friendId;
        this.message = message;
    }

    public AgreedFriendRequestMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            setFriendId(jsonObj.getString("friendId"));
            setMessage(jsonObj.getString("message"));
            if(jsonObj.has("user")){
                setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
        }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("friendId", friendId);
            jsonObj.put("message", message);

            if(getJSONUserInfo() != null)
                jsonObj.putOpt("user",getJSONUserInfo());

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public AgreedFriendRequestMessage(Parcel in) {
        setFriendId(ParcelUtils.readFromParcel(in));
        setMessage(ParcelUtils.readFromParcel(in));
        setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<AgreedFriendRequestMessage> CREATOR = new Creator<AgreedFriendRequestMessage>() {

        @Override
        public AgreedFriendRequestMessage createFromParcel(Parcel source) {
            return new AgreedFriendRequestMessage(source);
        }

        @Override
        public AgreedFriendRequestMessage[] newArray(int size) {
            return new AgreedFriendRequestMessage[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, message);
        ParcelUtils.writeToParcel(dest, friendId);
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
