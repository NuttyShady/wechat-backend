package com.shady.service;

import com.shady.bean.RespFormatter;

public interface UserService {

    /**
     * 根据openid查询用户
     * @param openid
     */
    RespFormatter getUserByOpenid(String openid);

    /**
     * 根据phoneNum查询用户
     * @param phoneNum
     * @param password
     */
    RespFormatter getUserByPhonePass(String phoneNum, String password);

    /**
     * 根据phoneNum绑定用户
     * @param phoneNum
     * @param openid
     */
    RespFormatter bindPhoneNum(String phoneNum, String openid);

    /**
     * 设置密码
     * @param password
     * @param sepPhone
     */
    RespFormatter setPassword(String password, String sepPhone);

    /**
     * 检查巡检进度是否按照顺序
     * @param taskUUID
     * @param inspectionNum
     */
    RespFormatter checkInspection(String taskUUID, int inspectionNum);

    /**
     * 提交巡检进度
     * @return
     * @param taskUUID
     * @param inspectionNum
     */
    RespFormatter submitInspection(String taskUUID, int inspectionNum);
}
