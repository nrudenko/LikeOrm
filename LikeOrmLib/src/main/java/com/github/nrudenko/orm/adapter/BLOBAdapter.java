package com.github.nrudenko.orm.adapter;

import android.database.Cursor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serialize adapter for stores data in byte array
 */
public class BLOBAdapter implements SerializeAdapter<byte[]> {
    @Override
    public byte[] serialize(Object object) {
        byte[] byteArray = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            bos.close();
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArray;
    }

    @Override
    public Object deserialize(Cursor cursor, int columnIndex, Class modelClass) {
        Object object = null;
        final byte[] byteArray = cursor.getBlob(columnIndex);
        if (byteArray != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bis);
                object = ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    bis.close();
                    if (ois != null)
                        ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
}
