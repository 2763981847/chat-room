package com.example.socket.common.util;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.example.socket.common.model.entity.LoginUser;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
public class DatabaseUtils {
    private static Db db = Db.use();

    public static LoginUser getUserByName(String name) {
        LoginUser loginUser;
        try {
            loginUser = entityToUser(db.findAll(Entity.create("user").set("name", name)).get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loginUser;
    }

    public static Long saveUser(LoginUser loginUser) {
        Long id;
        try {
            id = db.insertForGeneratedKey(Entity.create("user").parseBean(loginUser));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static List<LoginUser> listUsers() {
        List<Entity> entities;
        try {
            entities = db.findAll("user");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entities.stream().map(DatabaseUtils::entityToUser).toList();
    }

    private static LoginUser entityToUser(Entity entity) {
        return entity.toBean(LoginUser.class);
    }
}
