package com.rongseal.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rongseal.App;
import com.rongseal.R;
import com.rongseal.bean.Friend;

import java.util.List;

/**
 * Created by AMing on 15/11/3.
 * Company RongCloud
 */
public class FriendAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;

    private List<Friend> list;

    public FriendAdapter(Context context , List<Friend> list) {
        this.context = context;
        this.list = list;
    }

    /** 传入新的数据 刷新UI的方法 */
    public void updateListView(List<Friend> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        final Friend mContent = list.get(position);
        if (convertView== null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.frienduri);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getLetters());
        }else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        viewHolder.tvTitle.setText(this.list.get(position).getName());
        String url = this.list.get(position).getUri();
        if(!TextUtils.isEmpty(url)){
            ImageLoader.getInstance().displayImage(url, viewHolder.mImageView, App.getOptions());
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getLetters().charAt(0);
    }

    final static class ViewHolder{
        /** 首字母 */
        TextView tvLetter;
        /** 昵称 */
        TextView tvTitle;
        /** 头像 */
        ImageView mImageView;
        /** userid */
        TextView tvUserId;
    }


    /**
     * 提取英文的首字母，非英文字母用#代替。unused
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
}
