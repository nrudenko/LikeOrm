package com.githab.nrudenko.orm.example.model;

import java.util.Date;

public class ExampleModel extends BaseModel {

    String text;
    Date date;
    int intVal;

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

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }
}
