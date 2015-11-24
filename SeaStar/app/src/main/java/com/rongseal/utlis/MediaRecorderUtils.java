package com.rongseal.utlis;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class MediaRecorderUtils {

    private final String tag = MediaRecorderUtils.class.getSimpleName();
    private String mFileName;
    private MediaPlayer mPlayer;
    private MediaRecorder mediaRecorder;
    private OnCompletionListener onCompletionListener;

    /**
     * 构造方法
     */
    public MediaRecorderUtils() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.amr";
    }

    /**
     * 录制音频
     * @param start
     */
    public void onRecord(boolean start) {
        if (start) {
            startRecord();
        } else {
            stopRecord();
        }
    }

    private void startRecord(){
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            File audioFile = new File(mFileName);
            if(audioFile.exists()){
                audioFile.deleteOnExit();
            }
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord(){
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     * @param start
     */
    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    /**
     * 开始播放
     */
    private void startPlaying() {
        try {
            mPlayer = new MediaPlayer();
            if (onCompletionListener != null) {
                mPlayer.setOnCompletionListener(this.onCompletionListener);
            }
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(tag, "prepare() failed");
        }
    }

    /**
     * 停止播放
     */
    private void stopPlaying() {
        if(mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 释放资源
     */
    public void release(){
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return onCompletionListener;
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }
}
