package com.hanmlet.myblog.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 *
 * @author hanml
 */
@Setter
@Getter
public class UserDTO  {

    private String username;
    private String displayName;
    private long userId;
    private String mobile;
    private String email;
    private String password;
    private String sex;
    private String avatar;
    private String signature;
    private String website;

}
