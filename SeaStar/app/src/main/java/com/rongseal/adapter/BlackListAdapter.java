package com.rongseal.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rongseal.App;
import com.rongseal.R;
import com.rongseal.utlis.DialogWithYesOrNoUtils;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.utils.NToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;


/**
 * Created by AMing on 15/11/30.
 * Company RongCloud
 */
public class BlackListAdapter extends BaseAdapter {


    public static final java.lang.String BLACKLISTUI = "BLACKLISTUI";
    private ViewHolder holder;

    private UserInfo bean;

    public BlackListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.blacklist_item, null);
            holder.mHead = (ImageView) convertView.findViewById(R.id.black_head);
            holder.mName = (TextView) convertView.findViewById(R.id.black_username);
            holder.mId = (TextView) convertView.findViewById(R.id.black_userid);
            holder.mRemove = (Button) convertView.findViewById(R.id.remove_blacklist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bean = (UserInfo) getDataSet().get(position);
        if (!TextUtils.isEmpty(bean.getPortraitUri() + "")) {
            ImageLoader.getInstance().displayImage(bean.getPortraitUri() + "", holder.mHead, App.getOptions());
        }
        holder.mName.setText(bean.getName());
        holder.mId.setText(bean.getUserId());
        holder.mRemove.setTag(bean.getUserId());
        holder.mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String removeId = (String) v.getTag();
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否确定移除黑名单", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        if (RongIM.getInstance() != null && !TextUtils.isEmpty(removeId)) {
                            RongIM.getInstance().getRongIMClient().removeFromBlacklist(removeId, new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    BroadcastManager.getInstance(mContext).sendBroadcast(BLACKLISTUI);
                                    NToast.shortToast(mContext, "移除成功");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                        }
                    }
                });

            }
        });
        return convertView;
    }


    class ViewHolder {
        ImageView mHead;
        TextView mName;
        TextView mId;
        Button mRemove;
    }
}
