package com.wuqihang.symcservermanager.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wuqihang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private boolean admin;
    private boolean _super;
    private String username;
    private String password;
}
