package com.shady.controller;

import com.shady.bean.BackendBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private BackendBean backendBean;

    @RequestMapping("/getuser")
    public String getUser() {
        return "user~~~"+backendBean.getAuthor();
    }
}
