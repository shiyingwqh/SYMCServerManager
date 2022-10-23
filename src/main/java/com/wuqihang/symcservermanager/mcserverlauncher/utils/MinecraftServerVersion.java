package com.wuqihang.symcservermanager.mcserverlauncher.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wuqihang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinecraftServerVersion {
    private String id;
    private String type;
    private String url;
    private String time;
    private String releaseTime;
    private String sha1;
    private String complianceLevel;
}
