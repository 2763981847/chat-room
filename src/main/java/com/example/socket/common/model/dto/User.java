package com.example.socket.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    // 用户名
    private String name;
    // socket
    private Socket socket;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
