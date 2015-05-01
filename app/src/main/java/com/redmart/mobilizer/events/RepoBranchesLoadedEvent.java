package com.redmart.mobilizer.events;

import com.redmart.mobilizer.models.Branch;

import java.util.List;

/**
 * Created by Joshua on 27/3/15.
 */
public class RepoBranchesLoadedEvent {
    private List<Branch> branches;

    public RepoBranchesLoadedEvent(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Branch> getBranches() {
        return branches;
    }
}
