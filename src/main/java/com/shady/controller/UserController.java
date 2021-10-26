package com.shady.controller;

import com.google.gson.Gson;
import com.shady.bean.RespJson;
import com.shady.config.WechatWebSocket;
import com.shady.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
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
        RespJson respJson = userService.getUserByOpenid(openid);
        return new Gson().toJson(respJson);
    }

    @RequestMapping("/phonelogin")
    public String phoneNumLogin(HttpServletRequest request) {
        System.out.println("=====\nRequest: " + request.getRequestURI());
        String phoneNum = request.getParameter("phoneNum");
        String password = request.getParameter("password");
        RespJson respJson = userService.getUserByPhonePass(phoneNum, password);
        return new Gson().toJson(respJson);
    }

    @RequestMapping("/bind")
    public String bindPhone(@RequestParam String phoneNum, @RequestParam String openid) {
        System.out.println("=====\nBind Phone Number: " + phoneNum);
        System.out.println("Bind openid: " + openid);
        RespJson respJson = userService.bindPhoneNum(phoneNum, openid);
        return new Gson().toJson(respJson);
    }

    @RequestMapping("/setpassword")
    public String setPass(HttpServletRequest request) {
        System.out.println("=====\nRequest: " + request.getRequestURI());
        String sepPass = request.getParameter("password");
        String sepPhone = request.getParameter("phoneNum");
        RespJson respJson = userService.setPassword(sepPass, sepPhone);
        return new Gson().toJson(respJson);
    }

    /**
     * 测试方法：
     *  1.浏览器中打开index.html（WebSocket页面）并点击Send获取如“Hello, 1634648821014”信息
     *  2.访问http://localhost:8080/debug/websocket?userid=1634648821014
     *  3.观察WebSocket页面
     * @param userid js中根据时间戳生成的websocket识别码（临时）
     */
    @RequestMapping("/debug/websocket")
    public void wsTest(@RequestParam String userid) {
        WechatWebSocket wws = new WechatWebSocket();
        Map<String, WechatWebSocket> clients = WechatWebSocket.getClients();
        for (WechatWebSocket item : clients.values()) {
            if(item.getUserid().equals(userid))
                wws.sendMessageTo("Greeting from backend!", userid);
        }
    }
}
