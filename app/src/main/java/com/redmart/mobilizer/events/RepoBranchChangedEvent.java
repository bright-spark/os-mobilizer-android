package com.redmart.mobilizer.events;

/**
 * Created by Joshua on 26/3/15.
 */
public class RepoBranchChangedEvent {
    private String branch;

    public RepoBranchChangedEvent(String branch) {
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }
}
