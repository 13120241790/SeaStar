package com.rongseal.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.rongseal.R;
import com.rongseal.widget.cyclepager.ADInfo;
import com.rongseal.widget.cyclepager.CycleViewPager;
import com.rongseal.widget.cyclepager.ViewFactory;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

import static com.rongseal.R.*;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class SealFragment extends Fragment implements View.OnClickListener {
    public static SealFragment instance = null;

    public static SealFragment getInstance() {
        if (instance == null) {
            instance = new SealFragment();
        }
        return instance;
    }

    private View mView;

    private Button mChatRoom1, mChatRoom2, mChatRoom3, mChatRoom4, mChatRoom5;

    private List<ImageView> views = new ArrayList<ImageView>();
    private List<ADInfo> infos = new ArrayList<ADInfo>();
    private CycleViewPager cycleViewPager;

    private String[] imageUrls = {"http://c.picphotos.baidu.com/album/s%3D1000%3Bq%3D90/sign=0f4bd53487cb39dbc5c06356e0263255/37d3d539b6003af3e8086dbe332ac65c1138b6ba.jpg",
            "http://f.picphotos.baidu.com/album/s%3D1000%3Bq%3D90/sign=b09156f508f41bd5de53ecf461eababa/359b033b5bb5c9ead841b23dd339b6003bf3b3ba.jpg",
            "http://d.picphotos.baidu.com/album/s%3D1000%3Bq%3D90/sign=4cc6202c7b1ed21b7dc92ae59d5ee6b5/d52a2834349b033b90e74d0413ce36d3d439bd85.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3141659685,1124697251&fm=15&gp=0.jpg", "http://b.picphotos.baidu.com/album/s%3D1000%3Bq%3D90/sign=884c8dbbb519ebc4c4787299b216f48d/00e93901213fb80e8a91d30930d12f2eb83894a8.jpg"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ss_seastar_activity, null);
        initChatRoom();
        configImageLoader();
        initImagePager();
        return mView;
    }

    private void initChatRoom() {
        mChatRoom1 = (Button) mView.findViewById(id.chatroom1);
        mChatRoom2 = (Button) mView.findViewById(id.chatroom2);
        mChatRoom3 = (Button) mView.findViewById(id.chatroom3);
        mChatRoom4 = (Button) mView.findViewById(id.chatroom4);
        mChatRoom5 = (Button) mView.findViewById(id.chatroom5);
        mChatRoom1.setOnClickListener(this);
        mChatRoom2.setOnClickListener(this);
        mChatRoom3.setOnClickListener(this);
        mChatRoom4.setOnClickListener(this);
        mChatRoom5.setOnClickListener(this);
    }

    private void initImagePager() {
        cycleViewPager = (CycleViewPager) getChildFragmentManager().findFragmentById(id.fragment_cycle_viewpager_content);
        for (int i = 0; i < imageUrls.length; i++) {
            ADInfo info = new ADInfo();
            info.setUrl(imageUrls[i]);
            info.setContent("图片-->" + i);
            infos.add(info);
        }

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(getActivity(), infos.get(infos.size() - 1).getUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(getActivity(), infos.get(i).getUrl()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(getActivity(), infos.get(0).getUrl()));

        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true);

        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, mAdCycleViewListener);
        //设置轮播
        cycleViewPager.setWheel(true);

        // 设置轮播时间，默认5000ms
        cycleViewPager.setTime(2000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }


    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
                Toast.makeText(getActivity(),
                        "position-->" + info.getContent(), Toast.LENGTH_SHORT)
                        .show();
            }

        }

    };

    private void configImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(drawable.icon_stub) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(drawable.icon_stub) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.chatroom1:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, "chatroom001", "Java");
                break;
            case id.chatroom2:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, "chatroom002", "Python");
                break;
            case id.chatroom3:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, "chatroom003", "Objective-C");
                break;
            case id.chatroom4:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, "chatroom004", "Android");
                break;
            case id.chatroom5:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, "chatroom005", "PHP");
                break;
        }
    }
}
