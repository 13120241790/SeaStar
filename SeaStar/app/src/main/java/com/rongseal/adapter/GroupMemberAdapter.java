package com.rongseal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.rongseal.R;
import com.rongseal.bean.response.GetGroupInfoResponse;

/**
 * Created by AMing on 15/11/25.
 * Company RongCloud
 */
public class GroupMemberAdapter extends BaseAdapter{

    private DisplayImageOptions options;

    public GroupMemberAdapter(Context context) {
        super(context);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.rc_default_portrait)
                .showImageOnFail(R.drawable.rc_default_portrait)
                .showImageOnLoading(R.drawable.rc_default_portrait)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private ViewHolder holder;


    class ViewHolder{
        ImageView mMemberHead;
        TextView  mMemberName;
        TextView  mMemberId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.group_member_item,null);
            holder.mMemberHead = (ImageView) convertView.findViewById(R.id.member_img_head);
            holder.mMemberName = (TextView) convertView.findViewById(R.id.member_name);
            holder.mMemberId = (TextView) convertView.findViewById(R.id.member_id);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        GetGroupInfoResponse.ResultEntity.UsersEntity bean = (GetGroupInfoResponse.ResultEntity.UsersEntity) dataSet.get(position);
        ImageLoader.getInstance().displayImage(bean.getPortrait(),holder.mMemberHead, options);
        holder.mMemberName.setText(bean.getUsername());
        holder.mMemberId.setText(bean.getId());
        return convertView;
    }
}
