package com.example.socket.common.model.dto;

import java.io.Serializable;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
public enum MessageType implements Serializable {

    ENTER, EXIT, MESSAGE, IMAGE, FILE, USERS, GROUPS, NEW_GROUP;
}
