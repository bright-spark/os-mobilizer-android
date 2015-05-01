package com.redmart.mobilizer.fragments;

import android.app.Fragment;

import com.redmart.mobilizer.utils.BusProvider;

/**
 * Created by Joshua on 26/3/15.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
