package com.rupeng.Fragment;

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
public class RuPengFragment extends Fragment {
    public static RuPengFragment instance = null;

    public static RuPengFragment getInstance() {
        if (instance == null) {
            instance = new RuPengFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("如鹏");
        tv.setGravity(1);
        return tv;
    }
}
