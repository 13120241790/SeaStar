package com.rongseal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.bean.response.SearchUserNameResponse;

/**
 * Created by AMing on 15/11/13.
 * Company RongCloud
 */
public class SearchListAdapter extends BaseAdapter<SearchUserNameResponse.ResultEntity>{


    private ViewHolder holder;

    private Context mContext;

    public SearchListAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.searchlist_item_layout, null);
            holder.mHead = (ImageView) convertView.findViewById(R.id.search_img_head);
            holder.mUserId = (TextView) convertView.findViewById(R.id.tv_userid);
            holder.mUserName = (TextView) convertView.findViewById(R.id.tv_username);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchUserNameResponse.ResultEntity bean = dataSet.get(position);

        holder.mUserName.setText(bean.getUsername());
        holder.mUserId.setText("id:"+bean.getId());

//        if (holder.mHead != null) {
//            Picasso.with(convertView.getContext())
//                    .load(bean.getPortrait())
//                    .placeholder(R.drawable.rp_default_head)
//                    .into(holder.mHead);
//        }
        return convertView;
    }

    class ViewHolder{
        ImageView mHead;
        TextView mUserName;
        TextView mUserId;

    }
}
