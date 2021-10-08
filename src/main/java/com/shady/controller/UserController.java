package com.shady.controller;

import com.google.gson.Gson;
import com.shady.dao.UserDao;
import com.shady.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/getuser")
    public String getUser(@RequestParam(value = "name", defaultValue = "Shady") String name) {  //RequestParam接收URL中参数
        return "user: "+name;
    }

    @RequestMapping("/login")
    public String dologin(@RequestParam String openid) {
        System.out.println("=====\nOpenID: " + openid);
        List<UserDao> userList = userService.getByOpenid(openid);
        String json = new Gson().toJson(userList);
        return json;
    }

    @RequestMapping("/bind")
    public String bindPhone(@RequestParam String phoneNum, @RequestParam String openid) {
        System.out.println("=====\nBind Phone Number: " + phoneNum);
        System.out.println("Bind openid: " + openid);
        Boolean bindFlag = false;
        try {
            bindFlag = userService.bindPhoneNum(phoneNum, openid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if(bindFlag) return "SUCCESS";
        else return "ERROR";
    }

    @RequestMapping("/setpassword")
    public String setPass(@RequestBody String requestBody) {
        System.out.println("=====\nReceived body: " + requestBody);
        String sepPass = requestBody.split("&")[0].split("=")[1];
        String sepPhone = requestBody.split("&")[1].split("=")[1];
        Boolean setFlag = false;
        try {
            setFlag = userService.setPassword(sepPass, sepPhone);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if(setFlag) return "SUCCESS";
        else return "ERROR";
    }
}
