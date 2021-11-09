package com.shady.service.impl;

import com.shady.bean.RespFormatter;
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
import java.util.Iterator;
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
    public RespFormatter getUserByOpenid(String openid) {
        RespFormatter resp;
        List<UserDao> users = null;
        try {
            users = jdbcTemplate.query("select * from `staff_all` where openID = ?", this::getUserList, openid);
        } catch (DataAccessException e) {
            String info = String.format("***Login*** Failed logging in for openid: %s , exception found: %s.", openid, e.getMessage());
            resp = setResp("ERROR", info, null);
            return resp;
        }
        if (users.size() != 0) {
            String info = String.format("***Login*** Login success for openid: %s .", openid);
            resp = setResp("SUCCESS",info, users);
        } else {
            String info = String.format("***Login*** Failed logging in for openid: %s , 0 hit found.", openid);
            resp = setResp("ERROR", info, null);
        }
        return resp;
    }

    @Override
    public RespFormatter getUserByPhonePass(String phoneNum, String password) {
        RespFormatter resp;
        List<UserDao> users = null;
        try {
            users = jdbcTemplate.query("select * from `staff_all` where phoneNum = ?", this::getUserList, phoneNum);
        } catch (DataAccessException e) {
            String info = String.format("***Login*** Failed logging in for phone number: %s , exception found: %s.", phoneNum, e.getMessage());
            resp = setResp("ERROR", info, null);
            return resp;
        }
        Iterator<UserDao> it = users.iterator();
        if (it.hasNext()) {
            if (it.next().getPassword().equals(password)) {
                String info = String.format("***Login*** Login success for phone number: %s .", phoneNum);
                resp = setResp("SUCCESS",info, users);
            } else {
                String info = String.format("***Login*** Failed logging in for phone number: %s , incorrect password.", phoneNum);
                resp = setResp("ERROR", info, null);
            }
        } else {
            String info = String.format("***Login*** Failed logging in for phone number: %s , 0 hit found.", phoneNum);
            resp = setResp("ERROR", info, null);
        }
        return resp;
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
    public RespFormatter bindPhoneNum(String phoneNum, String openid) {
        AtomicReference<RespFormatter> resp = new AtomicReference<>(new RespFormatter());
        List<UserDao> users = null;
        //匹配手机号
        jdbcTemplate.query("select count(*) from `staff_all` where phoneNum = ?", (rs, i) -> {
            UserDao user = new UserDao();
            if (rs.getInt(1) != 0) {
                //将获取到的openid存入该条目下
                int setFlag = jdbcTemplate.update("update `staff_all` set openid = ? where phoneNum = ?", openid, phoneNum);
                if (setFlag == 1) {
                    String info = String.format("***Bind*** Open ID %s has bound with phone number: %s .", openid, phoneNum);
                    resp.set(setResp("SUCCESS", info, null));
                } else {
                    String info = String.format("***Bind*** Failed to bind Open ID %s with phone number: %s .", openid, phoneNum);
                    resp.set(setResp("ERROR", info, null));
                }
            } else {
                String info = String.format("***Bind*** Failed to bind Open ID %s with phone number: %s , phone number not found.", openid, phoneNum);
                resp.set(setResp("ERROR", info, null));
            }
            return user;
        }, phoneNum);
        return resp.get();
    }

    @Override
    public RespFormatter setPassword(String password, String phoneNum) {
        RespFormatter resp;
        String base64encodedPass = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        int setFlag = jdbcTemplate.update("update `staff_all` set password = ?, isFirstLogin = 0 where phoneNum = ?", base64encodedPass, phoneNum);
        if(setFlag == 1) {
            String info = String.format("***Password*** Password set for phone number: %s .", phoneNum);
            resp = setResp("SUCCESS", info, null);
        } else {
            String info = String.format("***Password*** Failed to set password for phone number: %s .", phoneNum);
            resp = setResp("ERROR", info, null);
        }
        return resp;
    }

    @Override
    public RespFormatter checkInspection(String taskUUID, int inspectionNum) {
        RespFormatter resp = null;
        List<Integer> result = jdbcTemplate.query("select route_done from `inspection` where taskUUID = ?", (rs, i) -> rs.getInt(1), taskUUID);
        int routeDone = result.get(0);
        if (routeDone + 1 == inspectionNum) {
            String info = String.format("***Inspection*** Inspection for task %s reached checkpoint %d .", taskUUID, inspectionNum);
            resp = setResp("SUCCESS", info, null);
        } else {
            String info = String.format("***Inspection*** Disordered inspection for task %s on checkpoint %d .", taskUUID, inspectionNum);
            resp = setResp("ERROR", info, null);
        }
        return resp;
    }

    @Override
    public RespFormatter submitInspection(String taskUUID, int inspectionNum) {
        RespFormatter resp;
        int setFlag = jdbcTemplate.update("update `inspection` set route_done = ? where taskUUID = ?", inspectionNum, taskUUID);
        if(setFlag == 1) {
            String info = String.format("***Inspection*** Inspection for task %s completed checkpoint %d .", taskUUID, inspectionNum);
            resp = setResp("SUCCESS", info, null);
            List<Integer> result = jdbcTemplate.query("select route_total from `inspection` where taskUUID = ?", (rs, i) -> rs.getInt(1), taskUUID);
            int routeTotal = result.get(0);
            //完成全部检查点
            if (inspectionNum == routeTotal) {
                info = String.format("***Inspection*** Inspection for task %s completed all %d checkpoint.", taskUUID, routeTotal);
                resp = setResp("SUCCESS", info, null);
                jdbcTemplate.update("update `inspection` set state = 1, actualEndTime = NOW() where taskUUID = ?", taskUUID);
            }
        } else {
            String info = String.format("***Inspection*** Task %s failed to submit at checkpoint %d", taskUUID, inspectionNum);
            resp = setResp("ERROR", info, null);
        }
        return resp;
    }

    private RespFormatter setResp(String status, String info, List<UserDao> users) {
        RespFormatter resp = new RespFormatter();
        log.info(info);
        resp.setStatus(status);
        resp.setInfo(info);
        resp.setData(users);
        return resp;
    }
}
