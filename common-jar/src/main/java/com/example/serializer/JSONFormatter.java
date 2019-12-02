package com.example.serializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

/**
 * JSONFormatter converts objects to JSON representation and vice-versa. This
 * class depends on Google's GSON library to do the transformation. This class
 * is not thread-safe.
 * Extracted from Paypal
 */
/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 * extracted from Paypal
 */
public final class JSONFormatter {

    /**
     * FieldNamingPolicy used by the underlying Gson library. Alter this
     * property to set a fieldnamingpolicy other than
     * LOWER_CASE_WITH_UNDERSCORES used by PayPal REST APIs
     */
    private static FieldNamingPolicy FIELD_NAMING_POLICY = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
    /**
     * Gson
     */
    public static Gson GSON = new GsonBuilder()
              .setPrettyPrinting()
//            .registerTypeAdapter(Date.class, new DateSerializer())
            .setExclusionStrategies(new GsonExclusionStrategy())//Added By Msh 9608
            .setFieldNamingPolicy(FIELD_NAMING_POLICY).create();

    public static Gson GSON_FULL = new GsonBuilder()
            .setPrettyPrinting()
//            .registerTypeAdapter(Date.class, new DateSerializer())
            .setFieldNamingPolicy(FIELD_NAMING_POLICY).create();

    /*
     * JSONFormatter is coupled to the stubs generated using the SDK generator.
     * Since PayPal REST APIs support only JSON, this class is bound to the
     * stubs for their json representation.
     */

    /**
     * Gson
     */
    public static Gson GSON_LOG = new GsonBuilder()
//            .setPrettyPrinting()
            .setExclusionStrategies(new GsonExclusionStrategy())//Added By Msh 9608
            .setExclusionStrategies(new GsonExclusionStrategyLog())//Added By Msh 9608
            .setFieldNamingPolicy(FIELD_NAMING_POLICY).create();

    public static Gson GSON_ELASTIC = new GsonBuilder()
//            .setPrettyPrinting()
            .registerTypeAdapter(Date.class, new DateSerializer())
            .setExclusionStrategies(new GsonExclusionStrategy())//Added By Msh 9608
            .setExclusionStrategies(new GsonExclusionStrategyLog())//Added By Msh 9608
            .setFieldNamingPolicy(FIELD_NAMING_POLICY).create();
    /*
     * JSONFormatter is coupled to the stubs generated using the SDK generator.
     * Since PayPal REST APIs support only JSON, this class is bound to the
     * stubs for their json representation.
     */
    private JSONFormatter() {
    }

    /**
     * Set a format for gson FIELD_NAMING_POLICY. See {@link FieldNamingPolicy}
     *
     * @param FIELD_NAMING_POLICY
     */
    public static final void setFIELD_NAMING_POLICY(
            FieldNamingPolicy FIELD_NAMING_POLICY) {
        GSON = new GsonBuilder().setPrettyPrinting()
                .setFieldNamingPolicy(FIELD_NAMING_POLICY).create();
    }

    /**
     * Converts a Raw Type to JSON String
     *
     * @param <T> Type to be converted
     * @param t   Object of the type
     * @return JSON representation
     */
    public static <T> String toJSONLog(T t) {
        if (t != null)
            return GSON_LOG.toJson(t);
        return "{}";
    }
    public static <T> String toJSONFull(T t) {
        if (t != null)
            return GSON_FULL.toJson(t);
        return "{}";
    }
    public static <T> String toJSONElastic(T t) {
        if (t != null)
            return GSON_ELASTIC.toJson(t);
        return "{}";
    }
    public static <T> Map toMap(T t) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return GSON.fromJson(toJSON(t), type);
    }


    /**
     * Converts a Raw Type to JSON String
     *
     * @param <T> Type to be converted
     * @param t   Object of the type
     * @return JSON representation
     */
    public static <T> String toJSON(T t) {
        if (t != null)
            return GSON.toJson(t);
        return "{}";
    }

    /**
     * Converts a JSON String to object representation
     *
     * @param <T>            Type to be converted
     * @param responseString JSON representation
     * @param clazz          Target class
     * @return Object of the target type
     */
    public static <T> T fromJSON(String responseString, Class<T> clazz) {
        T t = null;
        if (clazz.isAssignableFrom(responseString.getClass())) {
            t = clazz.cast(responseString);
        } else {
            t = GSON.fromJson(responseString, clazz);
        }
        return t;
    }

    public <T> T fromJSON(String json, Type typeOfT) throws JsonSyntaxException {
        if(json == null) {
            return null;
        } else {
            StringReader reader = new StringReader(json);
            T target = GSON.fromJson(reader, typeOfT);
            return target;
        }
    }

    public static boolean isJsonValid(String json) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonTree = parser.parse(json);
            return true;
        } catch (Exception ex) {

        }
        return false;
    }
}
