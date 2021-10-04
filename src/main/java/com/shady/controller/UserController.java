package com.shady.controller;

import com.google.gson.Gson;
import com.shady.dao.UserDao;
import com.shady.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String dologin(@RequestParam(value = "openid", defaultValue = "") String openid) {
        System.out.println(openid);
        List<UserDao> userList = userService.getByOpenid(openid);
        String json = new Gson().toJson(userList);
        return json;
    }
}
