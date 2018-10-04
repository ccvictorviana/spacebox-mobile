package br.com.spacebox.api.converters;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        try {
            Long time = Long.valueOf(element.getAsString());
            return new Date(time);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}