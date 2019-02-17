package com.avoscloud.chat.model;

/**
 * Created by lenovo on 2018/4/29.
 */

public class UserState {

    private String bandname;

    public UserState(String bandname){
        this.bandname = bandname;
    }

    public String getUserState() {
        return bandname;
    }

    public void setUserState(String bandname) {
        this.bandname = bandname;
    }
}
