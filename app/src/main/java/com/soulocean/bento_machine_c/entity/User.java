package com.soulocean.bento_machine_c.entity;


import cn.bmob.v3.BmobUser;

/**
 * @author soulo
 */
public class User extends BmobUser {
    private String name;
    private Object User_pic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getUser_pic() {
        return User_pic;
    }

    public void setUser_pic(Object user_pic) {
        User_pic = user_pic;
    }
}