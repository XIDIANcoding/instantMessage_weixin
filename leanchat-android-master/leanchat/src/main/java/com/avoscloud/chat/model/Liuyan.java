package com.avoscloud.chat.model;


public class Liuyan {
    private String liuyaner="";
    private String liuyanReceiver="";
    private String liuyancontent="";

    public Liuyan(String liuyaner, String liuyancontent, String liuyanReceiver) {
        this.liuyaner = liuyaner;
        this.liuyancontent = liuyancontent;
        this.liuyanReceiver = liuyanReceiver;
    }

    public Liuyan() {
    }

    public String getLiuyanReceiver() {
        return liuyanReceiver;
    }

    public void setLiuyanReceiver(String liuyanReceiver) {
        this.liuyanReceiver = liuyanReceiver;
    }

    public String getLiuyaner() {
        return liuyaner;
    }

    public String getLiuyancontent() {
        return liuyancontent;
    }

    public void setLiuyaner(String liuyaner) {
        this.liuyaner = liuyaner;
    }

    public void setLiuyancontent(String liuyancontent) {
        this.liuyancontent = liuyancontent;
    }
}
