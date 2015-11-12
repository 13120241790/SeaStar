package com.rongseal.activity;

import android.net.Uri;
import android.os.Bundle;

import com.rongseal.R;

import io.rong.imkit.tools.PhotoFragment;

/**
 * rongcloud 放大查看大图依赖 class
 */
public class PhotoActivity extends BaseActivity{

    PhotoFragment mPhotoFragment;
    Uri mUri;
    Uri mDownloaded;


    // TODO  保存功能未使用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_photo);
        initView();
        initData();
        setTitle("查看大图");
    }


    protected void initView() {
        mPhotoFragment = (PhotoFragment)getSupportFragmentManager().findFragmentById(R.id.photo_fragment);
    }

    protected void initData() {
        Uri uri = getIntent().getParcelableExtra("photo");
        Uri thumbUri = getIntent().getParcelableExtra("thumbnail");

        mUri = uri;
        if (uri != null)
            mPhotoFragment.initPhoto(uri, thumbUri, new PhotoFragment.PhotoDownloadListener() {
                @Override
                public void onDownloaded(Uri uri) {
                    mDownloaded = uri;
                }

                @Override
                public void onDownloadError() {

                }
            });
    }

}
