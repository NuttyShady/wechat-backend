package com.shady.service.impl;

import com.shady.dao.UserDao;
import com.shady.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserServiceImpl implements UserService {

    private final JdbcTemplate jdbcTemplate;

    UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserDao> getByOpenid(String openid) {
        List<UserDao> users = null;
        try {
            users = jdbcTemplate.query("select * from `staff_all` where openID = ?", (resultSet, i) -> {
                UserDao user = new UserDao();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setIsFirstLogin(resultSet.getInt("isFirstLogin"));
                user.setOpenID(resultSet.getString("openID"));
                user.setPhoneNum(resultSet.getString("phoneNum"));
                return user;
            }, openid);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
        return users;
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
        int setFlag = jdbcTemplate.update("update `staff_all` set password = ? where phoneNum = ?", password, phoneNum);
        return setFlag == 1;
    }
}
