package com.rongseal.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import com.sd.core.common.PreferencesManager;

/**
 * [LoadDialog]
 * 
 * @author: devin.hu
 * @version: 1.0
 * @date: Oct 17, 2013
 */
public class GuideDialog extends Dialog {

	/**
	 * LoadDialog
	 */
	private static GuideDialog loadDialog;
	private Context mContext;
	private String pageKey;

	/**
	 * @param ctx
	 * @param key
	 */
	public GuideDialog(final Context ctx, String key) {
		super(ctx, android.R.style.Theme_Translucent_NoTitleBar);
		mContext = ctx;
		pageKey = key;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		PreferencesManager preferences = PreferencesManager.getInstance(mContext);
		preferences.put(pageKey, false);
	}

	@Override
	public void cancel() {
		super.cancel();
		PreferencesManager preferences = PreferencesManager.getInstance(mContext);
		preferences.put(pageKey, false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			cancel();
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 弹出dialog
	 * @param context
	 * @param key
	 * @param layoutId
	 */
	public static void show(Context context, String key, int layoutId) {
		PreferencesManager preferences = PreferencesManager.getInstance(context);
		boolean flag = preferences.get(key, true);
		if (flag) {
			if (loadDialog != null && loadDialog.isShowing()) {
				loadDialog.dismiss();
				loadDialog = null;
	        }
			loadDialog = new GuideDialog(context, key);
			loadDialog.setContentView(layoutId);
			loadDialog.show();
		}
	}
}
