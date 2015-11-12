/*
    ShengDao Android Client, ViewPagerAdapter
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package com.rongseal.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * [ViewPager适配器]
 *
 * 
 **/
public class ViewPagerAdapter extends PagerAdapter {

	private List<String> titles;
	private List<View> pageViews;
	
	public ViewPagerAdapter(List<View> pageViews) {
		this.pageViews = pageViews;
	}
	
	public CharSequence getPageTitle(int position) {
		return titles.get(position);
	}
	
	@Override  
     public int getCount() {  
         return pageViews.size();  
     }  

     @Override  
     public boolean isViewFromObject(View arg0, Object arg1) {  
         return arg0 == arg1;  
     }  

     @Override  
     public int getItemPosition(Object object) {  
         return super.getItemPosition(object);  
     }  

     @Override  
     public void destroyItem(View arg0, int arg1, Object arg2) {  
         ((ViewPager) arg0).removeView(pageViews.get(arg1));  
     }  

     @Override  
     public Object instantiateItem(View arg0, int arg1) {  
         ((ViewPager) arg0).addView(pageViews.get(arg1));  
         return pageViews.get(arg1);  
     }  

     @Override  
     public void restoreState(Parcelable arg0, ClassLoader arg1) {  

     }  

     @Override  
     public Parcelable saveState() {  
         return null;  
     }  

     @Override  
     public void startUpdate(View arg0) {  

     }  

     @Override  
     public void finishUpdate(View arg0) {  

     }
     
     public List<String> getTitles() {
 		return titles;
 	}

 	public void setTitles(List<String> titles) {
 		this.titles = titles;
 	}
}
