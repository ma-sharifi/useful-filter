package com.example.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 * extracted from paypal
 */
public class DateSerializer implements JsonSerializer<Date>
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(dateFormat.format(date));
    }

    public static void main(String... args){

        GsonBuilder builder= new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateSerializer());
        Gson gson = builder.create();
        String json =gson.toJson(new Date());
        System.out.println(json);

    }
}
