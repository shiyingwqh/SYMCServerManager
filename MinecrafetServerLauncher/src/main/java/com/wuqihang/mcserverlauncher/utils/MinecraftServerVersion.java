package com.wuqihang.mcserverlauncher.utils;

/**
 * @author Wuqihang
 */

public class MinecraftServerVersion {
    private String id;
    private String type;
    private String url;
    private String time;
    private String releaseTime;
    private String sha1;
    private String complianceLevel;

    public MinecraftServerVersion() {
    }

    public MinecraftServerVersion(String id, String type, String url, String time, String releaseTime, String sha1, String complianceLevel) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.time = time;
        this.releaseTime = releaseTime;
        this.sha1 = sha1;
        this.complianceLevel = complianceLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getComplianceLevel() {
        return complianceLevel;
    }

    public void setComplianceLevel(String complianceLevel) {
        this.complianceLevel = complianceLevel;
    }
}
