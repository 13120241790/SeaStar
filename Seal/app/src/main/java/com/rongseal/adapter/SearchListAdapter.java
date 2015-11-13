package com.rongseal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.rongseal.R;
import com.rongseal.bean.response.User;

/**
 * Created by AMing on 15/11/13.
 * Company RongCloud
 */
public class SearchListAdapter extends BaseAdapter<User>{


    private ViewHolder holder;

    public SearchListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.searchlist_item_layout, null);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return null;
    }

    class ViewHolder{


    }
}
