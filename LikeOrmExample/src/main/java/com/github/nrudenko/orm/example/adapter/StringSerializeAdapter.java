package com.github.nrudenko.orm.example.adapter;

import android.database.Cursor;

import com.github.nrudenko.orm.adapter.SerializeAdapter;
import com.github.nrudenko.orm.example.utils.GsonWrapper;

/**
 * Serialize adapter for stores data in byte array
 */
public class StringSerializeAdapter implements SerializeAdapter<String> {
    @Override
    public String serialize(Object object) {
        return GsonWrapper.getGson().toJson(object);
    }

    @Override
    public Object deserialize(Cursor cursor, int columnIndex, Class modelClass) {
        String objectString = cursor.getString(columnIndex);
        return GsonWrapper.getGson().fromJson(objectString, modelClass);
    }
}
