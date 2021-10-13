package com.shady.service;

import com.shady.dao.UserDao;

import java.util.List;

public interface UserService {

    /**
     * 根据openid查询用户
     * @param openid
     * @return 返回查询结果list
     */
    List<UserDao> getUserByOpenid(String openid);

    /**
     * 根据phoneNum查询用户
     * @param phoneNum
     * @param password
     * @return 返回查询结果list
     */
    List<UserDao> getUserByPhonePass(String phoneNum, String password);

    /**
     * 根据phoneNum绑定用户
     * @param phoneNum
     * @param openid
     */
    Boolean bindPhoneNum(String phoneNum, String openid);

    /**
     * 设置密码
     * @param password
     * @param sepPhone
     */
    Boolean setPassword(String password, String sepPhone);
}
