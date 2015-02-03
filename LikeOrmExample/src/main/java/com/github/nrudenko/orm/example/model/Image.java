package com.github.nrudenko.orm.example.model;

import java.io.Serializable;
import java.util.Date;

public class Image implements Serializable {
    String path;
    Date date;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
