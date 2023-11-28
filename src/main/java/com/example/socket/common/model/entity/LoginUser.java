package com.example.socket.common.model.entity;

import lombok.Data;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
@Data
public class LoginUser {
    // 用户id
    private Long id;
    // 用户名
    private String name;
    // 密码
    private String password;
}
