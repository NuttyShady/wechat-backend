package com.shady.bean;

import com.shady.dao.UserDao;

import java.util.List;

public class RespFormatter {
    private String status;
    private String info;
    private List<UserDao> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<UserDao> getData() {
        return data;
    }

    public void setData(List<UserDao> data) {
        this.data = data;
    }
}
