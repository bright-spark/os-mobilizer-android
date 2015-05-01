package com.redmart.mobilizer.models;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Joshua on 26/3/15.
 */
public class ReleaseCollection extends BaseModel {
    private HashMap<String, List<Release>> releases;
    private List<String> branches;

    public HashMap<String, List<Release>> getReleases() {
        return releases;
    }

    public void setReleases(HashMap<String, List<Release>> releases) {
        this.releases = releases;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }
}
