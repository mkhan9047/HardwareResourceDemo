package com.farooq.smartapp.utils;

import com.google.gson.Gson;

public class JsonUtils {

    public static Object jsonToPojo(String jsonString, Class<?> pojoClass) {
        return new Gson().fromJson(jsonString, pojoClass);
    }
}
