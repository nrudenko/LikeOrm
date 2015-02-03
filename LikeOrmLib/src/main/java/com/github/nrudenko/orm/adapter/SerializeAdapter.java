package com.github.nrudenko.orm.adapter;

import android.database.Cursor;

import java.io.Serializable;

/**
 *Interface for save in database serialize data
 * in special format. Implementation class should store
 * data in some type of database types.
 * For example: String or byte array.
 */
public interface SerializeAdapter<T extends Serializable> {
    public T serialize(Object object);
    public Object deserialize(Cursor cursor, int columnIndex, Class modelClass);
}
