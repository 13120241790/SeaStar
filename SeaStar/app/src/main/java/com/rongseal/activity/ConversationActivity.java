package com.rongseal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.bean.response.ScreenBean;
import com.rongseal.utlis.CommonUtils;
import com.rongseal.utlis.DialogWithYesOrNoUtils;
import com.rongseal.utlis.video.LocUtil;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.network.download.DownLoadCallback;
import com.sd.core.network.download.DownloadManager;
import com.sd.core.utils.NLog;
import com.sd.core.utils.NToast;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import static io.rong.imlib.model.Conversation.ConversationType.*;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class ConversationActivity extends BaseActivity {

    private static final int GROUPDETIALS = 1265;
    private Button mRightBtn;
    private ConversationType type;
    private String targetId;

    private RelativeLayout rlVideo;

    //----------------video 相关成员变量 ----------
    /**
     * 当前视频路径
     */
    private String path = "http://www.modrails.com/videos/passenger_nginx.mov";
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 最大音量
     */
    private int mMaxVolume;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;
    /**
     * 手势数目
     */
    private int finNum = 0;

    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private VideoView mVideoView;
    private GestureDetector gestDetector;
    private ScaleGestureDetector scaleDetector;

    private ScreenBean screenBean;
    // -----------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_conversation_activity);

        Intent intent = getIntent();
        if (intent == null || intent.getData() == null)
            return;

        isPushMessage(intent);
        targetId = getIntent().getData().getQueryParameter("targetId"); //获取id
        String name = getIntent().getData().getQueryParameter("title"); //获取昵称
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
        } else {
            setTitle(targetId);
        }
        //获取会话类型
        type = valueOf(getIntent().getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        rlVideo = (RelativeLayout) findViewById(R.id.rl_video);
        mRightBtn = getBtn_right();
        mRightBtn.setVisibility(View.VISIBLE);
        switch (type) {
            case GROUP:
                mRightBtn.setText("群组详情");
                mRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ConversationActivity.this, GroupAetailsActivity.class);
                        intent.putExtra("groupId", targetId);
                        startActivityForResult(intent, GROUPDETIALS);
                    }
                });
                break;
            case PRIVATE:
                mRightBtn.setText("用户详情");
                break;
            case CHATROOM:
                if (targetId.equals("chatroom001")) {

                    if (!LibsChecker.checkVitamioLibs(this))
                        return;

                    if (!CommonUtils.isNetworkConnected(mContext)) {
                        NToast.shortToast(mContext, "请检查网络...");
                        return;
                    }

                    if (!(CommonUtils.NETTYPE_WIFI == CommonUtils.getNetworkType(mContext))) {
                        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "检测到当前网络环境非WIFI环境,是否继续缓冲(消耗流量)?", new DialogWithYesOrNoUtils.DialogCallBack() {
                            @Override
                            public void exectEvent() {
                                layout_head.setVisibility(View.GONE);
                                rlVideo.setVisibility(View.VISIBLE);
                                LoadDialog.show(mContext, "正在缓冲文件...");
                                DownloadManager downloadMgr = DownloadManager.getInstance();
                                downloadMgr.addHandler("http://www.modrails.com/videos/passenger_nginx.mov");
                                downloadMgr.setDownLoadCallback(new DownLoadCallback() {
                                    @Override
                                    public void onLoading(String url, int bytesWritten, int totalSize) {
                                        super.onLoading(url, bytesWritten, totalSize);
                                    }

                                    @Override
                                    public void onSuccess(String url, String filePath) {
                                        super.onSuccess(url, filePath);
                                        LoadDialog.dismiss(mContext);
                                        path = filePath;
                                        initVideo();
                                    }

                                    @Override
                                    public void onFailure(String url, String strMsg) {
                                        super.onFailure(url, strMsg);
                                    }
                                });
                            }
                        });
                    } else {
                        layout_head.setVisibility(View.GONE);
                        rlVideo.setVisibility(View.VISIBLE);
                        LoadDialog.show(mContext, "正在缓冲文件...");
                        DownloadManager downloadMgr = DownloadManager.getInstance();
                        downloadMgr.addHandler("http://www.modrails.com/videos/passenger_nginx.mov");
                        downloadMgr.setDownLoadCallback(new DownLoadCallback() {
                            @Override
                            public void onLoading(String url, int bytesWritten, int totalSize) {
                                super.onLoading(url, bytesWritten, totalSize);
                            }

                            @Override
                            public void onSuccess(String url, String filePath) {
                                super.onSuccess(url, filePath);
                                LoadDialog.dismiss(mContext);
                                path = filePath;
                                initVideo();
                            }

                            @Override
                            public void onFailure(String url, String strMsg) {
                                super.onFailure(url, strMsg);
                            }
                        });
                    }

                }
                break;
        }
    }

    private void initVideo() {
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

        mMaxVolume = LocUtil.getMaxVolume(this); //最大音量
        gestDetector = new GestureDetector(this, new SingleGestureListener());
        scaleDetector = new ScaleGestureDetector(this,
                new MultiGestureListener());

        screenBean = LocUtil.getScreenPix(mContext);
        if (path == "") {
            return;
        } else {
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView
                    .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.setPlaybackSpeed(1.0f);
                        }
                    });
        }
    }


    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     *
     * @param intent
     */
    private void isPushMessage(Intent intent) {
        NLog.e("isPushMessage");
        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("push") != null) {
            NLog.e("isPushMessage2");
            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push").equals("true")) {
                String id = intent.getData().getQueryParameter("pushId");
                RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                LoadDialog.show(mContext);
                NLog.e("isPushMessage3");
                enterActivity();
            }

        } else {//通知过来
            //程序切到后台，收到消息后点击进入,会执行这里
            if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                LoadDialog.show(mContext);
                enterActivity();
            }
        }
    }

    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String tokenCache = sp.getString("token", "");
        if (!TextUtils.isEmpty(tokenCache)) {
            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
            finish();
        } else {
            RongIM.connect(tokenCache, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {
                    if (RongCloudEvent.getInstance() != null)
                        RongCloudEvent.getInstance().setConnectedListener();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }


    public void onEventMainThread(GroupAetailsActivity.FinishActivity fa) {
        finish();
    }

    public void onEventMainThread(UserDetailActivity.DeleteFriend df) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finNum = event.getPointerCount();

        if (1 == finNum) {
            gestDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    endGesture();
            }
        } else if (2 == finNum) {
            scaleDetector.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 视频缩放
     */
    public void changeLayout(int size) {
        mVideoView.setVideoLayout(size, 0);
    }

    /**
     * 声音大小
     *
     * @param percent
     */
    public void changeVolume(float percent) {
        if (mVolume == -1) {
            mVolume = LocUtil.getCurVolume(this);
            if (mVolume < 0)
                mVolume = 0;
            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        LocUtil.setCurVolume(this, index);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 亮度大小
     *
     * @param percent
     */
    public void changeBrightness(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;
            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 单点触屏
     *
     * @author jin
     */
    private class SingleGestureListener implements
            android.view.GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
//            Log.d("Fling", velocityY);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            if (2 == finNum) {
                return false;
            }

            float moldX = e1.getX();
            float moldY = e1.getY();
            float y = e2.getY();
            if (moldX > screenBean.getsWidth() * 9.0 / 10)// 右边滑动
                changeVolume((moldY - y) / screenBean.getsHeight());
            else if (moldX < screenBean.getsWidth() / 10.0)// 左边滑动
                changeBrightness((moldY - y) / screenBean.getsHeight());
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }
    }

    /**
     * 多点缩放
     *
     * @author jin
     */
    private class MultiGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub
            // 返回true ，才能进入onscale()函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub
            float oldDis = detector.getPreviousSpan();
            float curDis = detector.getCurrentSpan();
            if (oldDis - curDis > 50) {
                // 缩小
                changeLayout(0);
                Toast.makeText(ConversationActivity.this, "缩小", Toast.LENGTH_SHORT).show();
            } else if (oldDis - curDis < -50) {
                // 放大
                changeLayout(1);
                Toast.makeText(ConversationActivity.this, "放大", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
