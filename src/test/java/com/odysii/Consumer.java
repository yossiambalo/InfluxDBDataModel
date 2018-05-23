package com.odysii;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
public class Consumer {
    public static void main(String[]args){

        javax.json.JsonReader jsonReader = Json.createReader(new ByteArrayInputStream("JsonString".getBytes(StandardCharsets.UTF_8)));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
    }
}
