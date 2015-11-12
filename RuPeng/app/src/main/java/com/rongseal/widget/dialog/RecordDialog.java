/*
    Suneee Android Client, RecordDialog
    Copyright (c) 2014 Suneee Tech Company Limited
*/

package com.rongseal.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.utlis.MediaRecorderUtils;
import com.rongseal.widget.downtime.DownTimer;
import com.rongseal.widget.downtime.DownTimerListener;
import com.sd.core.utils.NToast;

/**
 * [录音控件]
 * 
 * @author huxinwu
 * @version 1.0
 * @date 2014-11-17
 * 
 **/
public class RecordDialog extends Dialog implements View.OnClickListener{

	private View view_line;
	private ImageView img_record_animation;
	private ImageView img_record_bg;
	private TextView tv_record_play;
	private Button btn_detail_cancel;
	private Button btn_detail_confirm;
	private Button btn_detail_record;
	private AnimationDrawable animation;
	private MediaRecorderUtils recorderUtils;
	private View.OnClickListener onRecordistener;
	private DownTimer downMinTimer;
	private DownTimer downMaxTimer;
	private boolean recordTimeFlag = false;
	
	
	public RecordDialog(Context context) {
		super(context);
	}
	
	 @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.getContext().setTheme(android.R.style.Theme_InputMethod);
        setContentView(R.layout.layout_dialog_record);
        
        Window window  = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.4f;
        
        @SuppressWarnings("deprecation")
		int width = (int) (window.getWindowManager().getDefaultDisplay().getWidth()*0.85);
        window.setLayout(width, LayoutParams.WRAP_CONTENT); 
        
        downMinTimer = new DownTimer();
        downMinTimer.setListener(new DownTimerListener() {
			@Override
			public void onTick(long millisUntilFinished) {
				recordTimeFlag = false;
			}
			@Override
			public void onFinish() {
				recordTimeFlag = true;
			}
		});
        
        downMaxTimer = new DownTimer();
        downMaxTimer.setListener(new DownTimerListener() {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				animation.stop();
				recorderUtils.onRecord(false);
				
				view_line.setVisibility(View.VISIBLE);
				btn_detail_record.setVisibility(View.GONE);
				img_record_animation.setVisibility(View.GONE);
				btn_detail_confirm.setVisibility(View.VISIBLE);
				btn_detail_confirm.setText(R.string.submit_text);
				btn_detail_cancel.setVisibility(View.VISIBLE);
				img_record_bg.setVisibility(View.VISIBLE);
				tv_record_play.setVisibility(View.VISIBLE);
				
				NToast.shortToast(getContext(), R.string.record_time_max_text);
			}
		});
        
        recorderUtils = new MediaRecorderUtils();
        img_record_animation = (ImageView) findViewById(R.id.img_record_animation);
        img_record_bg = (ImageView) findViewById(R.id.img_record_bg);
        tv_record_play = (TextView) findViewById(R.id.tv_record_play);
        tv_record_play.setOnClickListener(this);
        animation = (AnimationDrawable) img_record_animation.getBackground();
        btn_detail_cancel = (Button) findViewById(R.id.btn_detail_cancel);
        btn_detail_cancel.setOnClickListener(this);
        btn_detail_cancel.setVisibility(View.GONE);
    	view_line = (View) findViewById(R.id.view_line);
    	view_line.setVisibility(View.GONE);
    	btn_detail_confirm = (Button) findViewById(R.id.btn_detail_confirm);
    	btn_detail_confirm.setOnClickListener(this);
    	btn_detail_confirm.setVisibility(View.GONE);
    	btn_detail_record = (Button) findViewById(R.id.btn_detail_record);
    	btn_detail_record.setVisibility(View.VISIBLE);
    	btn_detail_record.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) { 
				    case MotionEvent.ACTION_DOWN: 
				    	animation.stop();
				    	animation.start();
				    	if(downMinTimer != null){
				    		downMinTimer.stopDown();
				    	}
				    	downMinTimer.startDown(2000);
				    	
				    	if(downMaxTimer != null){
				    		downMaxTimer.stopDown();
				    	}
				    	downMaxTimer.startDown(5000);
				    	recorderUtils.onRecord(true);
				    	break;
				    
				    case MotionEvent.ACTION_UP: 
				    	animation.stop();
						recorderUtils.onRecord(false);
						
						if(downMaxTimer != null){
							downMaxTimer.stopDown();
						}
						
						if(recordTimeFlag){
							view_line.setVisibility(View.VISIBLE);
							btn_detail_record.setVisibility(View.GONE);
							img_record_animation.setVisibility(View.GONE);
							btn_detail_confirm.setVisibility(View.VISIBLE);
							btn_detail_confirm.setText(R.string.submit_text);
							btn_detail_cancel.setVisibility(View.VISIBLE);
							img_record_bg.setVisibility(View.VISIBLE);
							tv_record_play.setVisibility(View.VISIBLE);
						}else{
							NToast.shortToast(getContext(), R.string.record_time_text);
						}
				    	break;
				}
				return false;
			}
		});
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_detail_cancel:
				this.dismiss();
				break;
				
			case R.id.btn_detail_confirm:
				String txt = btn_detail_confirm.getText().toString();
				if(getContext().getString(R.string.submit_text).equals(txt)){
					if(onRecordistener != null){
						v.setTag(recorderUtils.getFileName());
						onRecordistener.onClick(v);
						dismiss();
					}
				}else{
					view_line.setVisibility(View.GONE);
					btn_detail_confirm.setVisibility(View.GONE);
					btn_detail_cancel.setVisibility(View.GONE);
					btn_detail_record.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.tv_record_play:
				String playtxt = tv_record_play.getText().toString();
				if("play".equals(playtxt)){
					recorderUtils.onPlay(true);
					tv_record_play.setText("puse");
					tv_record_play.setBackgroundResource(R.drawable.ic_record_puse);
				}else{
					recorderUtils.onPlay(false);
					tv_record_play.setText("play");
					tv_record_play.setBackgroundResource(R.drawable.ic_record_play);
				}
				break;
		}
	}

	public View.OnClickListener getOnRecordistener() {
		return onRecordistener;
	}

	public void setOnRecordistener(View.OnClickListener onRecordistener) {
		this.onRecordistener = onRecordistener;
	}
	
	
}
