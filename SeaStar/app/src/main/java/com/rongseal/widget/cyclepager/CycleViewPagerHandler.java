package com.rongseal.widget.cyclepager;

import android.content.Context;
import android.os.Handler;

/**
 * 为了防止内存泄漏，定义外部类，防止内部类对外部类的引用
 */
public class CycleViewPagerHandler extends Handler {
	 Context context;

	public CycleViewPagerHandler(Context context) {
		this.context = context;
	}
};