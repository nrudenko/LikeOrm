package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.annotation.Table;

import java.util.Date;

@Table
public class ExampleModel {

    String text;
    Date date;
    int intVal;

    public ExampleModel(String text) {
        this.text = text;
    }
}
