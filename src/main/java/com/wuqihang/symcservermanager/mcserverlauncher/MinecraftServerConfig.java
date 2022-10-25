package com.wuqihang.symcservermanager.mcserverlauncher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.util.Objects;

/**
 * @author Wuqihang
 */
public class MinecraftServerConfig {
    @JsonIgnore
    private int id;
    private String name;
    private String javaPath;
    private String jarPath;
    private String jvmParam;
    private String comment;
    private String serverHomePath;

    public MinecraftServerConfig() {
    }

    public MinecraftServerConfig(int id, String name, String javaPath, String jarPath, String jvmParam, String comment, String serverHomePath) {
        this.id = id;
        this.name = name;
        this.javaPath = javaPath;
        this.jarPath = jarPath;
        this.jvmParam = jvmParam;
        this.comment = comment;
        this.serverHomePath = serverHomePath;
    }

    public boolean isLegal() {
        if (StringUtils.isEmptyOrWhitespace(name) ||
                StringUtils.isEmptyOrWhitespace(jarPath) ||
                StringUtils.isEmptyOrWhitespace(serverHomePath)) {
            return false;
        }
        return new File(jarPath).exists() && new File(javaPath).exists();
    }

    public static void copy(MinecraftServerConfig dest, MinecraftServerConfig src) {
        dest.name = src.name;
        dest.jarPath = src.jarPath;
        dest.javaPath = src.javaPath;
        dest.jvmParam = src.jvmParam;
        dest.comment = src.comment;
        dest.serverHomePath = src.serverHomePath;
    }

    public static MinecraftServerConfig copy(MinecraftServerConfig src) {
        MinecraftServerConfig minecraftServerConfig = new MinecraftServerConfig();
        minecraftServerConfig.setId(-1);
        copy(minecraftServerConfig, src);
        return minecraftServerConfig;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getJvmParam() {
        return jvmParam;
    }

    public void setJvmParam(String jvmParam) {
        this.jvmParam = jvmParam;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getServerHomePath() {
        return serverHomePath;
    }

    public void setServerHomePath(String serverHomePath) {
        this.serverHomePath = serverHomePath;
    }

    @Override
    public String toString() {
        return "MinecraftServerConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", javaPath='" + javaPath + '\'' +
                ", jarPath='" + jarPath + '\'' +
                ", jvmParam='" + jvmParam + '\'' +
                ", comment='" + comment + '\'' +
                ", serverHomePath='" + serverHomePath + '\'' +
                '}';
    }
}
