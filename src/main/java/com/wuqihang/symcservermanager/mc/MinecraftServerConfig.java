package com.wuqihang.symcservermanager.mc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wuqihang
 */
@Data
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
}
