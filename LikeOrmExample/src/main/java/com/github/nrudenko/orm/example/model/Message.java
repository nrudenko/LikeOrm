package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.annotation.Table;

import java.util.Date;

@Table
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
