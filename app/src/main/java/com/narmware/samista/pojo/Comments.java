package com.narmware.samista.pojo;

/**
 * Created by rohitsavant on 29/08/18.
 */

public class Comments {

    String comment,flag,name,date_time;

    public Comments(String comment, String flag, String name, String date_time) {
        this.comment = comment;
        this.flag = flag;
        this.name = name;
        this.date_time = date_time;
    }

    public String getComment() {
        return comment;
    }
    public String getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public String getDate_time() {
        return date_time;
    }
}
