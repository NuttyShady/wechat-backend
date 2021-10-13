package com.shady.service.impl;

import com.shady.dao.UserDao;
import com.shady.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
            return null;
        }
        return users;
    }

    @Override
    public List<UserDao> getUserByPhonePass(String phoneNum, String password) {
        List<UserDao> users = null;
        try {
            users = jdbcTemplate.query("select * from `staff_all` where phoneNum = ? AND password = ?", this::getUserList, phoneNum, password);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
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
            user.setIsFirstLogin(resultSet.getInt("isFirstLogin"));
        } catch (SQLException e) {
            e.printStackTrace();
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
                jdbcTemplate.update("update `staff_all` set openid = ? where phoneNum = ?", openid, phoneNum);
                bindFlag.set(true);
            }
            return user;
        }, phoneNum);
        return bindFlag.get();
    }

    @Override
    public Boolean setPassword(String password, String phoneNum) throws DataAccessException {
        int setFlag = jdbcTemplate.update("update `staff_all` set password = ?, isFirstLogin = 0 where phoneNum = ?", password, phoneNum);
        return setFlag == 1;
    }
}
