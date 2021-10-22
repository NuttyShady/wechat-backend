package com.shady.service.impl;

import com.shady.dao.UserDao;
import com.shady.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final JdbcTemplate jdbcTemplate;

    UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserDao> getUserByOpenid(String openid) {
        List<UserDao> users = null;
        try {
            users = jdbcTemplate.query("select * from `staff_all` where openID = ?", this::getUserList, openid);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
        if (users.size() == 0) {
            log.info(String.format("***Login*** Failed logging in for openid: %s , 0 hit found.", openid));
        } else {
            log.info(String.format("***Login*** Login success for openid: %s .", openid));
        }
        return users;
    }

    @Override
    public List<UserDao> getUserByPhonePass(String phoneNum, String password) {//TODO 增加登陆失败原因，如密码不匹配等
        List<UserDao> users = null;
        String base64encodedPass = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        try {
            users = jdbcTemplate.query("select * from `staff_all` where phoneNum = ? AND password = ?", this::getUserList, phoneNum, base64encodedPass);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
        if (users.size() == 0) {
            log.info(String.format("***Login*** Failed logging in for phone number: %s , 0 hit found.", phoneNum));
        } else {
            log.info(String.format("***Login*** Login success for phone number: %s .", phoneNum));
        }
        return users;
    }

    private UserDao getUserList(ResultSet resultSet, int i) {
        UserDao user = new UserDao();
        try {
            user.setId(resultSet.getInt("id"));
            user.setOpenID(resultSet.getString("openID"));
            user.setParentID(resultSet.getInt("parentID"));
            user.setStaffUUID(resultSet.getString("staffUUID"));
            user.setName(resultSet.getString("name"));
            user.setPhoneNum(resultSet.getString("phoneNum"));
            user.setPosition(resultSet.getString("position"));
            user.setRemarks(resultSet.getString("remarks"));
            String base64decodedPass = new String (Base64.getDecoder().decode(resultSet.getString("password")), StandardCharsets.UTF_8);
            user.setPassword(base64decodedPass);
            user.setIsFirstLogin(resultSet.getInt("isFirstLogin"));
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return user;
    }

    @Override
    public Boolean bindPhoneNum(String phoneNum, String openid) throws DataAccessException {
        List<UserDao> users = null;
        AtomicReference<Boolean> bindFlag = new AtomicReference<>(false);
        //匹配手机号
        jdbcTemplate.query("select count(*) from `staff_all` where phoneNum = ?", (rs, i) -> {
            UserDao user = new UserDao();
            if (rs.getInt(1) != 0) {
                //将获取到的openid存入该条目下
                int setFlag = jdbcTemplate.update("update `staff_all` set openid = ? where phoneNum = ?", openid, phoneNum);
                if (setFlag == 1) {
                    log.info(String.format("***Bind*** Open ID %s has bound with phone number: %s .", openid, phoneNum));
                    bindFlag.set(true);
                } else {
                    log.info(String.format("***Bind*** Failed to bind Open ID %s with phone number: %s .", openid, phoneNum));
                }
            } else {
                log.info(String.format("***Bind*** Failed to bind Open ID %s with phone number: %s , phone number not found.", openid, phoneNum));
            }
            return user;
        }, phoneNum);
        return bindFlag.get();
    }

    @Override
    public Boolean setPassword(String password, String phoneNum) throws DataAccessException {
        String base64encodedPass = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        int setFlag = jdbcTemplate.update("update `staff_all` set password = ?, isFirstLogin = 0 where phoneNum = ?", base64encodedPass, phoneNum);
        if(setFlag == 1) {
            log.info(String.format("***Password*** Password set for phone number: %s .", phoneNum));
            return true;
        } else {
            log.info(String.format("***Password*** Failed to set password for phone number: %s .", phoneNum));
            return false;
        }
    }
}
