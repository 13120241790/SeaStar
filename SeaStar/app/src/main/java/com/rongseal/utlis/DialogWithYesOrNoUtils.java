package com.rongseal.utlis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


/**
 * Created by AMing on 15/11/26.
 * Company RongCloud
 */
public class DialogWithYesOrNoUtils {

    public void showDialog(Context context, String titleInfo, final DialogWithYesOrNoUtils.DialogCallBack callBack) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(context);
        alterDialog.setMessage(titleInfo);
        alterDialog.setCancelable(true);

        alterDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO 执行这个 exectEvent
                callBack.exectEvent();
            }
        });
        alterDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alterDialog.show();
    }

    interface DialogCallBack {
        void exectEvent();
    }
}
