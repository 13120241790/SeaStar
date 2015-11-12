package com.rongseal.widget.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rongseal.R;


/**   
 * [MessageDialog]
 * @author: devin.hu
 * @version: 1.0
 * @date:   Oct 17, 2013    
 */
public class MessageDialog extends BaseDialog {

	/** TextView对象 **/
	private TextView tv_message;
	/** View对象 **/
	private View contentView;
	/** 能否取消 true表示不能取消，false表示可以取消 **/
	private boolean canNotCancel = false;
    
	
	/**
	 * 可以取消、默认确定和取消、默认标题的MessageDialog
	 * @param context
	 * @param message
	 */
	public MessageDialog(Context context, String message) {
		this(context, null, context.getString(R.string.common_confirm), context.getString(R.string.common_cancel), null, message, false);
	}
	
	/**
	 * 可以取消、默认确定和取消、默认标题的MessageDialog
	 * @param context
	 * @param message
	 */
	public MessageDialog(Context context, String message, boolean canNotCancel) {
		this(context, null, context.getString(R.string.common_confirm), context.getString(R.string.common_cancel), null, message, canNotCancel);
	}
	
	/**
	 * 可以取消、默认确定和取消的MessageDialog
	 * @param context
	 * @param title
	 * @param message
	 */
	public MessageDialog(Context context, String title, String message) {
		this(context, title, context.getString(R.string.common_confirm), null, null, message, false);
	}
	
	/**
	 * 可以取消的MessageDialog
	 * @param context
	 * @param title
	 * @param btnText1
	 * @param btnText2
	 * @param message
	 */
	public MessageDialog(Context context, String title, String btnText1, String btnText2, String message) {
		this(context, title, btnText1, btnText2,  null, message,  false);
	}
	
	/**
	 * 构造方法
	 * @param context
	 * @param title
	 * @param btnText1
	 * @param btnText2
	 * @param message
	 * @param canNotCancel
	 */
	@SuppressLint("InflateParams") 
	public MessageDialog(Context context, String title, String btnText1, String btnText2, String btnText3, String message, boolean canNotCancel) {
		super(context);
		this.canNotCancel = canNotCancel;
		contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_message, null);
		tv_message = (TextView) contentView.findViewById(R.id.tv_message);
		
		if (!TextUtils.isEmpty(title)) {
			this.setTitle(title);
		}
		
		if(!TextUtils.isEmpty(message)){
			tv_message.setText(message);
		}
		
		if(!TextUtils.isEmpty(btnText1)){
			this.setBtn1Text(btnText1);
		}
		
		if(TextUtils.isEmpty(btnText2)){
			this.setBtn2Visible(false);
		} else {
			this.setBtn2Visible(true);
			this.setBtn2Text(btnText2);
		}
		
		if(TextUtils.isEmpty(btnText3)){
			this.setBtn3Visible(false);
		}else{
			this.setBtn3Visible(true);
			this.setBtn3Text(btnText3);
		}
	}

	@Override
	public View createContentView() {
		return contentView;
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
            if (canNotCancel) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
	
	/**
	 * 设置提示消息
	 * @param text 消息文本
	 */
	@SuppressWarnings("unused")
	private void setMessage(String text) {
		setMessage(text, false);
	}

	/**
	 * 设置提示消息
	 * @param text 消息文本
	 * @param isHtml 是否Html显示
	 */
	private void setMessage(String text, boolean isHtml) {
		if (isHtml) {
			tv_message.setText(Html.fromHtml(text));
		} else {
			tv_message.setText(text);
		}
	}
}
