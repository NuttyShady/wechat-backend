package com.shady.service;

import com.shady.dao.UserDao;

import java.util.List;

public interface UserService {

    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return 返回查询结果list
     */
    List<UserDao> getByOpenid(String openid);

    /**
     * 根据phoneNum绑定用户
     *
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
