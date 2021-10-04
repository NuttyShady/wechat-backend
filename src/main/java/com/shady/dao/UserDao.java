package com.shady.dao;

import javax.persistence.*;

//@Component
//@ConfigurationProperties(prefix = "backend")
//@PropertySource(value = {"classpath:backend.properties"})
@Entity
@Table(name = "staff_all")
public class UserDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Integer id;
    @Column(name = "openID")
    private String openID;
    @Column(name = "parentID")
    private Integer parentID;
    @Column(name = "staffUUID")
    private String staffUUID;
    @Column
    private String name;
    @Column(name = "phoneNum")
    private String phoneNum;
    @Column
    private String position;
    @Column
    private String remarks;
    @Column
    private String password;
    @Column(name = "isFirstLogin")
    private Integer isFirstLogin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String open_id) {
        this.openID = open_id;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parent_id) {
        this.parentID = parent_id;
    }

    public String getStaffUUID() {
        return staffUUID;
    }

    public void setStaffUUID(String staff_uuid) {
        this.staffUUID = staff_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Integer is_first_login) {
        this.isFirstLogin = is_first_login;
    }
}
