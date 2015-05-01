package com.redmart.mobilizer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.redmart.mobilizer.utils.BusProvider;

/**
 * Created by Joshua on 26/3/15.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}
