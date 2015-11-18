package com.rongseal.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.bean.response.NewFriendsListResponse;

/**
 * Created by AMing on 15/11/17.
 * Company RongCloud
 */
public class NewFriendsAdapter extends BaseAdapter {

    private ViewHolder holder;

    public NewFriendsAdapter(Context context) {
        super(context);
    }

    OnItemButtonClick mOnItemButtonClick;

    public OnItemButtonClick getOnItemButtonClick() {
        return mOnItemButtonClick;
    }

    public void setOnItemButtonClick(OnItemButtonClick onItemButtonClick) {
        this.mOnItemButtonClick = onItemButtonClick;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.newfriend_item_layout, null);
            holder.mHead = (ImageView) convertView.findViewById(R.id.new_friend_img_head);
            holder.mName = (TextView) convertView.findViewById(R.id.nf_tv_username);
            holder.mId = (TextView) convertView.findViewById(R.id.nf_tv_userid);
            holder.mState = (TextView) convertView.findViewById(R.id.item_friend_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final NewFriendsListResponse.ResultEntity bean = (NewFriendsListResponse.ResultEntity) dataSet.get(position);
//        holder.mHead
        holder.mName.setText(bean.getUsername());
        holder.mId.setText(bean.getId());
        holder.mState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemButtonClick !=null)
                    mOnItemButtonClick.onButtonClick(position, v,Integer.parseInt(bean.getStatus()));
            }
        });
            switch (Integer.parseInt(bean.getStatus())){
                case 1://好友
                    holder.mState.setText("已添加");
                    holder.mState.setBackgroundDrawable(null);
                    break;
                case 2://请求添加
                    holder.mState.setText("请求添加");
                    holder.mState.setBackgroundDrawable(null);
                    break;
                case 3://请求被添加
                    holder.mState.setText("添加");
                    holder.mState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.de_add_friend_selector));
                    break;
                case 4://请求被拒绝
                    holder.mState.setText("请求被拒绝");
                    holder.mState.setBackgroundDrawable(null);
                    break;
                case 5://我被对方删除
                    holder.mState.setText("被删除");
                    holder.mState.setBackgroundDrawable(null);
                    break;

            }
        return convertView;
    }
    public interface OnItemButtonClick{
        boolean onButtonClick(int position, View view,int status);

    }

    class ViewHolder {
        ImageView mHead;
        TextView mName;
        TextView mId;
        TextView mState;
    }
}
