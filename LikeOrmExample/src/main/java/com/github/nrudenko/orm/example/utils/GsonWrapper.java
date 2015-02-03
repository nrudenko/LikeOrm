package com.github.nrudenko.orm.example.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GsonWrapper {
    private static Gson gson;

    protected GsonWrapper() {
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateAdapter())
                    .registerTypeAdapter(java.sql.Date.class, new DateAdapter())
                    .create();
        }
        return gson;
    }

    static class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Date result = null;
            if (jsonElement != null) {
                String stringDate = jsonElement.getAsString();
                if (stringDate.matches("[0-9]+")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(jsonElement.getAsLong() * 1000));
                    result = calendar.getTime();
                } else {
                    result = DateUtils.stringToDate(stringDate);
                }
            }
            return result;
        }

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonElement result = null;
            if (date != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DATABASE_DATE_FORMAT, Locale.getDefault());
                String stringDate = simpleDateFormat.format(date);
                result = new JsonPrimitive(stringDate);
            }
            return result;
        }
    }
}
