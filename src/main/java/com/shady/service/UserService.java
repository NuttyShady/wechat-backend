package com.shady.service;

import com.shady.dao.UserDao;

import java.util.List;

public interface UserService {
//    /**
//     * 新增一个用户
//     *
//     * @param name
//     * @param age
//     */
//    int create(String name, Integer age);

    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    List<UserDao> getByOpenid(String openid);

//    /**
//     * 根据name删除用户
//     *
//     * @param name
//     */
//    int deleteByName(String name);

}
