package com.redmart.mobilizer.events;

/**
 * Created by Joshua on 27/3/15.
 */
public class InstallButtonClickedEvent {
    private String downloadLink;

    public InstallButtonClickedEvent(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}
