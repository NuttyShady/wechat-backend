package com.shady.controller;

import com.google.gson.Gson;
import com.shady.config.WechatWebSocket;
import com.shady.dao.UserDao;
import com.shady.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/getuser")
    public String getUser(@RequestParam(value = "name", defaultValue = "Shady") String name) {  //RequestParam接收URL中参数
        return "user: "+name;
    }

    @RequestMapping("/idlogin")
    public String openidLogin(@RequestParam String openid) {
        System.out.println("=====\nOpenID: " + openid);
        List<UserDao> userList = userService.getUserByOpenid(openid);
        String json = new Gson().toJson(userList);
        return json;
    }

    @RequestMapping("/phonelogin")
    public String phoneNumLogin(HttpServletRequest request) {
        System.out.println("=====\nRequest: " + request.getRequestURI());
        String phoneNum = request.getParameter("phoneNum");
        String password = request.getParameter("password");
        List<UserDao> userList = userService.getUserByPhonePass(phoneNum, password);
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
    public String setPass(HttpServletRequest request) {
        System.out.println("=====\nRequest: " + request.getRequestURI());
        String sepPass = request.getParameter("password");
        String sepPhone = request.getParameter("phoneNum");
        Boolean setFlag = false;
        try {
            setFlag = userService.setPassword(sepPass, sepPhone);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if(setFlag) return "SUCCESS";
        else return "ERROR";
    }

//    @RequestMapping("/debug/websocket")
//    public void wsTest() throws JSONException, UnsupportedEncodingException {
//        WechatWebSocket ws = new WechatWebSocket();
//        JSONObject jo = new JSONObject();
//        jo.put("message", "Websocket debug message from backend!");
//        jo.put("From", "backend");
//        jo.put("To", "websockettest");
//        ws.onMessage(jo.toString(),null);
//    }
}
