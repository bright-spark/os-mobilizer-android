package com.redmart.mobilizer.utils;

import com.squareup.otto.Bus;

/**
 * Created by Joshua on 16/6/14.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
