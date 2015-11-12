package com.rongseal.widget.picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import io.rong.imkit.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by AMing on 15/10/13.
 * Company RongCloud
 */
public class PhotoInputProvider extends InputProvider.ExtendProvider {

    private static final int REQUEST_IMAGE = 2;
    private static final int MAX_SELECT = 9;
    private RongContext mContext;
    private ArrayList<Message> mMsgMap;
    private int mQueueSize = 0;
    /**
     * 实例化适配器。
     *
     * @param context 融云IM上下文。（通过 RongContext.getInstance() 可以获取）
     */
    public PhotoInputProvider(RongContext context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Drawable obtainPluginDrawable(Context context) {
        return context.getResources().getDrawable(io.rong.imkit.R.drawable.rc_ic_picture);
    }

    @Override
    public CharSequence obtainPluginTitle(Context context) {
        return "相册";
    }

    @Override
    public void onPluginClick(View view) {
        Intent intent = new Intent(mContext, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, MAX_SELECT);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            mMsgMap = new ArrayList<>();
            mQueueSize = 0;
            ArrayList<String> uris = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

            ArrayList<PictureState> al = new ArrayList<>();
            for (String item : uris) {
                al.add(new PictureState(item,false));
            }
            mQueueSize = al.size();
            for (PictureState ps : al) {
                getContext().executorBackground(new AttachRunnable(Uri.parse("file://"+ps.getPath()),ps.getIsSendOriginalImage()));
                Log.e("预览", "path:" + ps.getPath() + "是否发送原图:" + ps.getIsSendOriginalImage());
            }
        }else if(data != null && data.getSerializableExtra("PREVIEW_RESULT") != null){
                ArrayList<String> senderList = data.getStringArrayListExtra("PREVIEW_RESULT");
                ArrayList<String> picList = data.getStringArrayListExtra("PHOTO_RESILT");
                Log.e("预览", senderList.size() + "------" + picList.size()+"会话界面上");

            ArrayList<PictureState> al = new ArrayList<>();
            for(String s : senderList){
                al.add(new PictureState(s,false));
            }
                for (String s : picList) {
                    for (int i = 0 ; i < al.size() ; i++ ) {
                        if (al.get(i).getPath().contains(s)) {
                            al.get(i).setIsSendOriginalImage(true);
                        }
                    }
                }


            mMsgMap = new ArrayList<>();
            mQueueSize = 0;
            mQueueSize = al.size();
            for (PictureState ps : al) {
                getContext().executorBackground(new AttachRunnable(Uri.parse("file://"+ps.getPath()),ps.getIsSendOriginalImage()));
                Log.e("预览", "path:" + ps.getPath() + "是否发送原图:" + ps.getIsSendOriginalImage());
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    class AttachRunnable implements Runnable {

        Uri mUri;
        boolean isSendOriginalImage;

        public AttachRunnable(Uri uri,boolean isSendOriginalImage) {
            mUri = uri;
            this.isSendOriginalImage = isSendOriginalImage;
        }

        @Override
        public void run() {

            RLog.d(this, "AttachRunnable", "insert image and save to db, uri = " + mUri);
            final ImageMessage content = ImageMessage.obtain(mUri, mUri, isSendOriginalImage);
            Conversation conversation = getCurrentConversation();

            if(RongIM.getInstance() != null &&
                    RongIM.getInstance().getRongIMClient() != null &&
                    conversation != null) {
                RongIM.getInstance().getRongIMClient().insertMessage(conversation.getConversationType(),
                        conversation.getTargetId(),
                        null, content,
                        new RongIMClient.ResultCallback<Message>() {
                            @Override
                            public void onSuccess(Message message) {
                                RLog.d(this, "AttachRunnable", "onSuccess insert image");
                                message.setSentStatus(Message.SentStatus.SENDING);
                                RongIM.getInstance().getRongIMClient().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
                                mMsgMap.add(message);
                                if (mMsgMap.size() == mQueueSize) {
                                    RongContext.getInstance().executorBackground(new UploadRunnable(mMsgMap.get(0)));
                                    mMsgMap.remove(0);
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                RLog.d(this, "AttachRunnable", "onError insert image, error = " + e);
                            }
                        });
            }
        }
    }

    class UploadRunnable implements Runnable {
        Message msg;

        public UploadRunnable(Message msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            RLog.d(this, "UploadRunnable", "sendImageMessage");
            RongIM.getInstance().getRongIMClient().sendImageMessage(msg, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {
                    RLog.d(this, "UploadRunnable", "onAttached");
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode code) {
                    if(mMsgMap.size() != 0) {
                        RongContext.getInstance().executorBackground(new UploadRunnable(mMsgMap.get(0)));
                        mMsgMap.remove(0);
                    }
                }

                @Override
                public void onSuccess(Message message) {
                    RLog.d(this, "UploadRunnable", "onSuccess");
                    if(mMsgMap.size() != 0) {
                        RongContext.getInstance().executorBackground(new UploadRunnable(mMsgMap.get(0)));
                        mMsgMap.remove(0);
                    }
                }

                @Override
                public void onProgress(Message message, int progress) {

                }
            });
        }
    }

    class PictureState{
        private String path;
        private Boolean isSendOriginalImage;


        PictureState(String path , Boolean isSendOriginalImage){
            this.path = path;
            this.isSendOriginalImage = isSendOriginalImage;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Boolean getIsSendOriginalImage() {
            return isSendOriginalImage;
        }

        public void setIsSendOriginalImage(Boolean isSendOriginalImage) {
            this.isSendOriginalImage = isSendOriginalImage;
        }
    }
}
