package com.song.community.dao.mapper;

import com.song.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Kycni
 * @date 2022/2/20 22:35
 */
@Mapper
public interface UserMapper {
    /**
     * 根据id查询具体用户
     */
    User selectUserById (int id);

    /**
     * 根据用户名查询具体用户
     */
    User selectUserByName (String username);

    /**
     * 根据用户名查询具体用户
     */
    User selectUserByEmail (String email);

    /**
     * 插入用户
     */
    int insertUser(User user);

    /**
     * 更新用户激活状态
     */
    int updateUserStatus(int userId, int status);
}
