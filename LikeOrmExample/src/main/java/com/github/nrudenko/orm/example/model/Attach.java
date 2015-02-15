package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.Table;

@Table
public class Attach {
    int _id;
    String url;
    @DbColumn(additional = "UNIQUE ON CONFLICT REPLACE")
    String messageId;


    public Attach(String url, String messageId) {
        this.url = url;
        this.messageId = messageId;
    }
}
