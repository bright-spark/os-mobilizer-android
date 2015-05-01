package com.redmart.mobilizer.events;

/**
 * Created by Joshua on 26/3/15.
 */
public class NavigationMenuItemSelectedInternalEvent {
    private int position;

    public NavigationMenuItemSelectedInternalEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
