package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.adapter.BLOBAdapter;
import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.Table;
import com.github.nrudenko.orm.commons.DbType;
import com.github.nrudenko.orm.example.adapter.StringSerializeAdapter;

import java.util.Date;

@Table
public class Message {

    String text;
    Date date;
    @DbColumn(type = DbType.SERIALIZED, adapter = BLOBAdapter.class)
    Audio audio;
    @DbColumn(type = DbType.SERIALIZED, adapter = StringSerializeAdapter.class)
    Image image;

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

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
