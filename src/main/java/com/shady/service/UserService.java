package com.shady.service;

import com.shady.bean.RespJson;
import com.shady.dao.UserDao;

import java.util.List;

public interface UserService {

    /**
     * 根据openid查询用户
     * @param openid
     */
    RespJson getUserByOpenid(String openid);

    /**
     * 根据phoneNum查询用户
     * @param phoneNum
     * @param password
     */
    RespJson getUserByPhonePass(String phoneNum, String password);

    /**
     * 根据phoneNum绑定用户
     * @param phoneNum
     * @param openid
     */
    RespJson bindPhoneNum(String phoneNum, String openid);

    /**
     * 设置密码
     * @param password
     * @param sepPhone
     */
    RespJson setPassword(String password, String sepPhone);
}
