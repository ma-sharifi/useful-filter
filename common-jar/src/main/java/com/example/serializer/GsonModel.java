package com.example.serializer;


import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 * extracted from Paypal
 */
@EqualsAndHashCode
public class GsonModel {

    /**
     * Returns a JSON string corresponding to object state
     *
     * @return JSON representation
     */
    public String toJSON() {
        return JSONFormatter.toJSON(this);
    }

    public String toJSONFull() {
        return JSONFormatter.toJSONFull(this);
    }

    public String toJSONElastic() {
        return JSONFormatter.toJSONElastic(this);
    }

    public String toJSONLog() {
        return JSONFormatter.toJSONLog(this);
    }

    public Map toMAP() {
        return JSONFormatter.toMap(this);
    }

    @Override
    public String toString() {
        return toJSON();
    }

}
