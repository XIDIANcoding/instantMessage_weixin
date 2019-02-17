package com.avoscloud.chat.model;

/**
 * Created by lenovo on 2018/4/25.
 */

public class TaskItem {
    private String owner;
    private String title;
    private String time;
    private String taskid;

    public TaskItem(String owner,String title, String time,String taskId) {
        this.owner=owner;
        this.title = title;
        this.time = time;
        this.taskid = taskId;
    }
    public String getOwner() {
        return owner;
    }
    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getTaskid() {
        return taskid;
    }
}
