package com.wuqihang.symcservermanager.mc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thymeleaf.util.StringUtils;

import java.io.File;

/**
 * @author Wuqihang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MinecraftServerConfig {
    @JsonIgnore
    private int id;
    private String name;
    private String javaPath;
    private String jarPath;
    private String otherParam;
    private String comment;
    private String serverHomePath;

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
        dest.otherParam = src.otherParam;
        dest.comment = src.comment;
        dest.serverHomePath = src.serverHomePath;
    }

    public static MinecraftServerConfig copy(MinecraftServerConfig src) {
        MinecraftServerConfig minecraftServerConfig = new MinecraftServerConfig();
        minecraftServerConfig.setId(-1);
        copy(minecraftServerConfig, src);
        return minecraftServerConfig;
    }
}
