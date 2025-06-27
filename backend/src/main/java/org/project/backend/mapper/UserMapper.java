package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.User;

import java.util.List;


@Mapper
public interface UserMapper {
    @Select("SELECT id, real_name AS realName, username, password, roles, status, create_time AS createTime, update_time AS updateTime " +
            "FROM `user` " +
            "WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username,
                                   @Param("password") String password);

    @Select("SELECT " +
            "id, " +
            "real_name AS realName, " +
            "username, " +
            "password, " +
            "roles, " +
            "status, " +
            "create_time AS createTime, " +
            "update_time AS updateTime " +
            "FROM `user` " +
            "WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

}
