package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.Table;

@Table
public class Attach {

    String url;
    @DbColumn(additional = "UNIQUE ON CONFLICT REPLACE")
    String messageId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
