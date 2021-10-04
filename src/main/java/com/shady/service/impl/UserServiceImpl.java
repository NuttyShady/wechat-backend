package com.shady.service.impl;

import com.shady.dao.UserDao;
import com.shady.service.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private JdbcTemplate jdbcTemplate;

    UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserDao> getByOpenid(String openid) {
        List<UserDao> users = jdbcTemplate.query("select * from `staff_all` where openID = ?", (resultSet, i) -> {
            UserDao user = new UserDao();
            user.setId(resultSet.getInt("id"));
            user.setName(resultSet.getString("name"));
            user.setIsFirstLogin(resultSet.getInt("isFirstLogin"));
            user.setOpenID(resultSet.getString("openID"));
            user.setPhoneNum(resultSet.getString("phoneNum"));
            return user;
        }, openid);
        return users;
    }
}
