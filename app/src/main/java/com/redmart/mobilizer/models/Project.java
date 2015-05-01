package com.redmart.mobilizer.models;

/**
 * Created by Joshua on 26/3/15.
 */
public class Project extends BaseModel {
    private String name;
    private String repo;
    private String platform;
    private String uri;
    private boolean usesStore;
    private boolean isTesting;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isUsesStore() {
        return usesStore;
    }

    public void setUsesStore(boolean usesStore) {
        this.usesStore = usesStore;
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean isTesting) {
        this.isTesting = isTesting;
    }
}
