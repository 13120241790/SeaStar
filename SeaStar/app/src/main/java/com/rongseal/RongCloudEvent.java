package com.rongseal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;

import com.rongseal.activity.MyDetailActivity;
import com.rongseal.activity.PhotoActivity;
import com.rongseal.activity.UserDetailActivity;
import com.rongseal.activity.UserDetailAddActivity;
import com.rongseal.activity.ValidationMessageActivity;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Friend;
import com.rongseal.db.com.rongseal.database.FriendDao;
import com.rongseal.db.com.rongseal.database.GroupDao;
import com.rongseal.message.AgreedFriendRequestMessage;
import com.rongseal.widget.picture.PhotoInputProvider;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.utils.NToast;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imkit.widget.provider.VoIPInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;

/**
 * Created by AMing on 15/11/6.
 * Company RongCloud
 */
public class RongCloudEvent implements RongIM.ConversationBehaviorListener, RongIMClient.OnReceiveMessageListener, RongIM.ConversationListBehaviorListener, RongIM.UserInfoProvider, RongIM.GroupInfoProvider {

    public static final java.lang.String FRIEND_MESSAGE = "FRIEND_MESSAGE";
    public static final java.lang.String GONEREDDOT = "GONEREDDOT";
    public static final String REFRESHUI = "refreshUI";
    private static final Uri FRIENDURL = Uri.parse("http://pic39.nipic.com/20140308/6608733_201355110000_2.jpg");
    private static RongCloudEvent mRongCloudInstance;

    private Context mContext;

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }

    }


    public RongCloudEvent(Context mContext) {
        this.mContext = mContext;
        //初始化不需要 connect 就能 监听的 Listener
        initListener();

    }

    /**
     * init 后就能设置的监听
     */
    private void initListener() {
//        de.greenrobot.event.EventBus.getDefault().register(this);
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
        RongIM.setConversationListBehaviorListener(this);
        RongIM.setUserInfoProvider(this, true);
        RongIM.setGroupInfoProvider(this,true);
    }

    /**
     * 需要 rongcloud connect 成功后设置的 listener
     */
    public void setConnectedListener() {
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);

        //        扩展功能自定义  singleProvider 语音 voip 只支持单对单
        InputProvider.ExtendProvider[] singleProvider = {
                new PhotoInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new LocationInputProvider(RongContext.getInstance()),//地理位置
                new VoIPInputProvider(RongContext.getInstance()),// 语音通话
        };

        InputProvider.ExtendProvider[] muiltiProvider = {
                new PhotoInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new LocationInputProvider(RongContext.getInstance()),//地理位置
        };

        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, singleProvider);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.DISCUSSION, muiltiProvider);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CUSTOMER_SERVICE, muiltiProvider);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, muiltiProvider);
    }


    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    /**
     * 点击头像的监听
     *
     * @param context
     * @param conversationType
     * @param userInfo
     * @return
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String sId = sp.getString("userid","");
        if (sId.equals(userInfo.getUserId())) {
            Intent intent = new Intent(context, MyDetailActivity.class);
            intent.putExtra("userid",userInfo.getUserId());
            context.startActivity(intent);
        }else {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("userid",userInfo.getUserId());
            context.startActivity(intent);
        }
        return true;
    }

    /**
     * 会话界面长按头像的监听
     *
     * @param context
     * @param conversationType
     * @param userInfo
     * @return
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    /**
     * 会话界面点击消息的监听
     *
     * @param context
     * @param view
     * @param message
     * @return
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        if (message.getContent() instanceof ImageMessage) { //实现会话界面点击查看大图逻辑  依赖 PhotoActivity 和 其布局 以及 menu/de_fix_username.xml
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Intent intent = new Intent(context, PhotoActivity.class);
            intent.putExtra("photo", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
            if (imageMessage.getThumUri() != null)
                intent.putExtra("thumbnail", imageMessage.getThumUri());
            context.startActivity(intent);
        }
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    /**
     * 会话界面长按消息的监听
     *
     * @param context
     * @param view
     * @param message
     * @return false 走融云默认监听
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof ContactNotificationMessage) {
            BroadcastManager.getInstance(mContext).sendBroadcast(FRIEND_MESSAGE);
        } else {
            if (messageContent instanceof AgreedFriendRequestMessage) {
                AgreedFriendRequestMessage afrm = (AgreedFriendRequestMessage) messageContent;
                UserInfo userInfo = afrm.getUserInfo();
                if (userInfo != null) {
                    DBManager.getInstance(mContext).getDaoSession().getFriendDao().insertOrReplace(new Friend(userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri().toString()));
                    BroadcastManager.getInstance(mContext).sendBroadcast(RongCloudEvent.REFRESHUI);
                }
            }
        }
        RongIM.getInstance().getRongIMClient().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                EventBus.getDefault().post(integer);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        return false;
    }



    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        MessageContent messageContent = uiConversation.getMessageContent();
        if (messageContent instanceof ContactNotificationMessage) {
            context.startActivity(new Intent(context, ValidationMessageActivity.class));
            BroadcastManager.getInstance(context).sendBroadcast(GONEREDDOT);
            return true;
        }
        return false;
    }

    @Override
    public UserInfo getUserInfo(String s) {
        FriendDao friendDao =  DBManager.getInstance(mContext).getDaoSession().getFriendDao();
        Friend  bean = friendDao.queryBuilder().where(FriendDao.Properties.UserId.eq(s)).unique();
        if (bean != null) {
            return new UserInfo(bean.getUserId(),bean.getName(), Uri.parse(bean.getPortraitUri()));
        }else{
            //TODO http 请求网络
        }
        if (s.equals("10000")) {
            return new UserInfo("10000","好友验证消息",FRIENDURL);
        }
        return null;
    }

    @Override
    public Group getGroupInfo(String s) {
        GroupDao groupdao = DBManager.getInstance(mContext).getDaoSession().getGroupDao();
        com.rongseal.db.com.rongseal.database.Group bean = groupdao.queryBuilder().where(GroupDao.Properties.GroupId.eq(s)).unique();
        if (bean != null){
            return new Group(bean.getGroupId(),bean.getName(),Uri.parse("http://img5.imgtn.bdimg.com/it/u=1024504869,25874000&fm=21&gp=0.jpg"));
        }else {
            //TODO 根据 s 请求网络拉取数据
        }
        return null;
    }
}
