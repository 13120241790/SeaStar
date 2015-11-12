package com.rongseal.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class SealFragment extends Fragment {
    public static SealFragment instance = null;

    public static SealFragment getInstance() {
        if (instance == null) {
            instance = new SealFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("海豹");
        tv.setGravity(1);
        return tv;
    }
}
