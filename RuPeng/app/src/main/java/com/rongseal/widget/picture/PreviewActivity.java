package com.rongseal.widget.picture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.R;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by AMing on 15/10/18.
 * Company RongCloud
 */
public class PreviewActivity extends FragmentActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private ArrayList<String> previewList;  //相册界面传来用户选择的图片数据

    private ViewPager mViewPager;   // ViewPager

    private MyAdapter mMyAdapter;

    private ImageView mImageBack; //返回箭头

    private TextView mTextCount; // 当前第N张/总张数

    private Button mButtonSend;  // 发送字样

    private CheckBox mCheBoxSelect, mCheBoxPictrue; // 选择勾选框  原图勾选框

    private TextView mTextSize; // 原图大小

    private ArrayList<SaveSelectState> mSelectedPics; //记住最终选择的状态集合

    private ArrayList<SaveOriginalImageState> mOriginalImage; //记住最终原图勾选状态的集合

    private int mCurrentPicIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_pic_preview_activity);

        previewList = getIntent().getStringArrayListExtra("resultList");

        mSelectedPics = new ArrayList<SaveSelectState>();
        mOriginalImage = new ArrayList<SaveOriginalImageState>();
        if (previewList != null && previewList.size() > 0)
            for (int i = 0; i < previewList.size(); i++) {
                mSelectedPics.add(new SaveSelectState(Uri.parse(previewList.get(i)), true)); // 初始化全部默认为选择
                mOriginalImage.add(new SaveOriginalImageState(previewList.get(i),false));   //初始化全部默认为不发送原图
            }

        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.rc_pre_viewpager);
        mImageBack = (ImageView) findViewById(R.id.btn_back);
        mTextCount = (TextView) findViewById(R.id.rc_preview_string);
        mButtonSend = (Button) findViewById(R.id.commit);
        mTextCount.setText("1/" + previewList.size());
        mButtonSend.setText("发送" + "(" + previewList.size() + "/9)");
        mCheBoxPictrue = (CheckBox) findViewById(R.id.isOriginalImage);
        mCheBoxSelect = (CheckBox) findViewById(R.id.rc_pre_select);
        mTextSize = (TextView) findViewById(R.id.rc_pic_size);
        mTextSize.setVisibility(View.GONE);
        mImageBack.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);
        mCheBoxPictrue.setOnCheckedChangeListener(this);
        mCheBoxSelect.setOnCheckedChangeListener(this);
        mCheBoxSelect.setChecked(true); // 选择都默认选中
        mCheBoxPictrue.setChecked(false); //原图都默认不勾选

        if (previewList != null && previewList.size() > 0) {
            mMyAdapter = new MyAdapter(getSupportFragmentManager(), previewList);
            mViewPager.setAdapter(mMyAdapter);
            mViewPager.setOnPageChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> sendUriList = new ArrayList<>();
        for (SaveSelectState state : mSelectedPics) {
            if (state.isSelect() == true)
                sendUriList.add(state.getUri().getPath());
        }

        if (v.getId() == R.id.btn_back) {
            Intent mIntent = new Intent(PreviewActivity.this,MultiImageSelectorFragment.class);
            mIntent.putStringArrayListExtra("BACK_RESULT", sendUriList);
            PreviewActivity.this.setResult(7,mIntent);
            finish();
        } else if (v.getId() == R.id.commit) {//发送
            ArrayList<String> picList = new ArrayList<String>();
            for (SaveOriginalImageState state : mOriginalImage) {
                if (state.isSelect() == true)
                    picList.add(state.getPath());
            }

            Intent mIntent = new Intent(PreviewActivity.this,MultiImageSelectorFragment.class);
            mIntent.putStringArrayListExtra("PREVIEW_RESULT", sendUriList);
            mIntent.putStringArrayListExtra("PHOTO_RESILT", picList);
            PreviewActivity.this.setResult(7,mIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        ArrayList<String> sendUriList = new ArrayList<>();
        for (SaveSelectState state : mSelectedPics) {
            if (state.isSelect() == true)
                sendUriList.add(state.getUri().getPath());
        }

        Intent mIntent = new Intent(PreviewActivity.this,MultiImageSelectorFragment.class);
        mIntent.putStringArrayListExtra("BACK_RESULT", sendUriList);
        PreviewActivity.this.setResult(7, mIntent);
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.isOriginalImage) {//是否发送原图
            if (isChecked) {

                mOriginalImage.get(mCurrentPicIndex).setIsSelect(true);


                mTextSize.setText("(" + UriToSize(previewList.get(mCurrentPicIndex)) + ")");
                mTextSize.setVisibility(View.VISIBLE);
            }else {
                mOriginalImage.get(mCurrentPicIndex).setIsSelect(false);

                mTextSize.setVisibility(View.GONE);
            }
        } else if (buttonView.getId() == R.id.rc_pre_select) {
            if (isChecked) {
                mSelectedPics.get(mCurrentPicIndex).setIsSelect(true);
            } else {
                mSelectedPics.get(mCurrentPicIndex).setIsSelect(false);
                mOriginalImage.get(mCurrentPicIndex).setIsSelect(false);
                mCheBoxPictrue.setChecked(false);
            }

            int selected = 0;
            for (SaveSelectState state : mSelectedPics) {
                if (state.isSelect)
                    selected++;
            }
            mButtonSend.setText("发送" + "(" + selected + "/9)");
            setSendButState(selected);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPicIndex = position;
        int i = position;
        mTextCount.setText((i + 1) + "/" + previewList.size());


//        mTextSize.setVisibility(View.GONE);
//        mCheBoxPictrue.setChecked(false);

        if (mSelectedPics.get(position).isSelect() == true) {
            mCheBoxSelect.setChecked(true);
        } else {
            mCheBoxSelect.setChecked(false);
        }

        if (mOriginalImage.get(position).isSelect() == true) {
            mCheBoxPictrue.setChecked(true);
            mTextSize.setText("(" + UriToSize(previewList.get(position)) + ")");
            mTextSize.setVisibility(View.VISIBLE);
        } else {
            mCheBoxPictrue.setChecked(false);
        }

        int selected = 0;
        for (SaveSelectState state : mSelectedPics) {
            if (state.isSelect)
                selected++;
        }
        mButtonSend.setText("发送" + "(" + selected + "/9)");
        setSendButState(selected);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> al;

        public MyAdapter(FragmentManager fm, ArrayList<String> al) {
            super(fm);
            this.al = al;
        }

        @Override
        public Fragment getItem(int position) {
            return new PreviewFragment(PreviewActivity.this, Uri.parse("file://" + al.get(position)));
        }

        @Override
        public int getCount() {
            return al.size();
        }
    }

    class SaveSelectState {

        private Uri mUri;

        private Boolean isSelect;

        SaveSelectState(Uri mUri, Boolean isSelect) {
            this.mUri = mUri;
            this.isSelect = isSelect;
        }

        public Uri getUri() {
            return mUri;
        }

        public void setUri(Uri mUri) {
            this.mUri = mUri;
        }

        public Boolean isSelect() {
            return isSelect;
        }

        public void setIsSelect(Boolean isSelect) {
            this.isSelect = isSelect;
        }
    }

    class SaveOriginalImageState {

        private String sPath;

        private Boolean isSelect;

        SaveOriginalImageState(String sPath, Boolean isSelect) {
            this.sPath = sPath;
            this.isSelect = isSelect;
        }

        public String getPath() {
            return sPath;
        }

        public void setPath(String sPath) {
            this.sPath = sPath;
        }

        public Boolean isSelect() {
            return isSelect;
        }

        public void setIsSelect(Boolean isSelect) {
            this.isSelect = isSelect;
        }
    }

    /**
     * 通过传入的 本地路径 获取 图片大小
     * @param s 图片的存储路径
     * @return KB MB
     */
    public String UriToSize(String s){
        File file = new File(s);
        long blockSize = 0;
        try {
            blockSize = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }



    /**
     * 获取指定文件大小
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        else{
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }


    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    private void setSendButState(int i){
        if (i == 0) {
            mButtonSend.setTextColor(getResources().getColor(R.color.group_list_gray));
            mButtonSend.setClickable(false);
        }else {
            mButtonSend.setTextColor(getResources().getColor(R.color.white));
            mButtonSend.setClickable(true);
        }

    }

}
