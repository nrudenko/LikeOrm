package com.github.nrudenko.orm.example.model;

import java.util.Date;

public class Message extends BaseModel {

    String text;
    Date date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
