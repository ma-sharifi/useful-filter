package com.example.panache.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private final Map<String, Object> values = new HashMap();

    public Parameters() {
    }

    public Parameters and(String name, Object value) {
        this.values.put(name, value);
        return this;
    }

    public Map<String, Object> map() {
        return Collections.unmodifiableMap(this.values);
    }

    public static Parameters with(String name, Object value) {
        return (new Parameters()).and(name, value);
    }
}
