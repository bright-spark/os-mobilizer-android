package com.redmart.mobilizer.events;

/**
 * Created by Joshua on 28/3/15.
 */
public class UninstallButtonClickedEvent {
    private String packageName;

    public UninstallButtonClickedEvent(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }
}
