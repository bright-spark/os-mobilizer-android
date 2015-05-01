package com.redmart.mobilizer.models;

/**
 * Created by Joshua on 26/3/15.
 */
public class Release extends BaseModel {
    private long id;
    private String name;
    private String body;
    private String releaseDate;
    private String alphaDownloadLink;
    private String betaDownloadLink;
    private String alphaPackageName;
    private String betaPackageName;
    private String versionNumber;
    private String versionName;
    private boolean alpha;
    private boolean beta;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAlphaDownloadLink() {
        return alphaDownloadLink;
    }

    public void setAlphaDownloadLink(String alphaDownloadLink) {
        this.alphaDownloadLink = alphaDownloadLink;
    }

    public String getBetaDownloadLink() {
        return betaDownloadLink;
    }

    public void setBetaDownloadLink(String betaDownloadLink) {
        this.betaDownloadLink = betaDownloadLink;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public boolean isBeta() {
        return beta;
    }

    public void setBeta(boolean beta) {
        this.beta = beta;
    }

    public String getAlphaPackageName() {
        return alphaPackageName;
    }

    public void setAlphaPackageName(String alphaPackageName) {
        this.alphaPackageName = alphaPackageName;
    }

    public String getBetaPackageName() {
        return betaPackageName;
    }

    public void setBetaPackageName(String betaPackageName) {
        this.betaPackageName = betaPackageName;
    }
}
