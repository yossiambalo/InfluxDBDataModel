package com.odysii.influx;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class JsonHandler {
    private Logger LOGGER = Logger.getLogger(JsonHandler.class);
    private Map<String,String> event = new HashMap<>();
    private Map<String,String> line;
    private List<Map<String,String>> events = new ArrayList<>();
    private List<Map<String,String>> lines = new ArrayList<>();
    private Map<String,String> loyalities = new HashMap<>();
    private Map<String,String> loyality = new HashMap<>();
    private Map<String,String> playedItem;
    private List<Map<String,String>> playedItems = new ArrayList<>();
    private Map<String,String> actions = new HashMap<>();
    private Map<String,String> action = new HashMap<>();
    private List<Map<String,String>> tendrs = new ArrayList<>();
    private List<Map<String,String>> tender = new ArrayList<>();
    private Map<String,String> additionalData = new HashMap<>();

    public List<Map<String, String>> getPlayedItems() {
        return playedItems;
    }

    public JsonHandler(String payLoad, Map<String,String> additionalData){
        this.additionalData = additionalData;
        byte[] decodedBytes = Base64.decodeBase64(payLoad);
        String json = decompress(decodedBytes);
        getDatAndPopulateCollections(json);
    }
    public JsonHandler(String json){
        getDatAndPopulateCollections(json);
    }

    void post(String s)throws Exception{
        URL url = new URL("http://yossia:8070/CashRegisterService/AddToBasket");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);
        byte[] out = s.getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }
    public List<Map<String, String>> getEvents() {
        return events;
    }

    private void getDatAndPopulateCollections(String json) {
        JSONObject jsonObject = null;
       try {
           jsonObject = new JSONObject(json);
       }catch (JSONException e){
           LOGGER.error("Not a valid json: "+e.getMessage());
       }
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            JSONObject object = new JSONObject(jsonObject.get((String)iterator.next()).toString());
            collectData(object);
        }
    }

    private void collectData(JSONObject object) {
        event = new HashMap<>();
        Iterator iterator = object.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value =   object.get(key).equals(null) ? "" :  String.valueOf((object.get(key)));
           event.put(key,value);
        }
        events.add(event);
    }

    private void setPlayedItems(JsonArray jsonArray) {
        Iterator<JsonValue> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            playedItem = new HashMap<>();
            JsonObject jsonObject = (JsonObject) iterator.next();
            for (String key : jsonObject.keySet()) {
                if (!"Products".equals(key)) {
                    //System.out.println(key + ":" + jsonObject.get(key));
                    playedItem.put(key, jsonObject.getString(key));
                }
            }
            Iterator<JsonValue> iterator2 = jsonObject.getJsonArray("Products").iterator();
            JsonObject jsonObject2 = (JsonObject) iterator2.next();
            for (String key : jsonObject2.keySet()) {
                //System.out.println(key + ":" + jsonObject2.get(key));
                playedItem.put(key, jsonObject2.getString(key));
            }
            playedItem.putAll(additionalData);
            playedItems.add(playedItem);
        }
    }

    private void setLines(JsonArray jsonArray){
        Iterator<JsonValue> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            line = new HashMap<>();
            JsonObject jsonObject = (JsonObject) iterator.next();
            for (String key : jsonObject.keySet()) {
                System.out.println(key + ":" + jsonObject.get(key));
                line.put(key,jsonObject.getString(key));
            }
            lines.add(line);
        }
    }
    public static String decompress(byte[] compressed) {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        StringBuilder sb = null;
        try {
            GZIPInputStream gis = new GZIPInputStream(bis);
            BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            gis.close();
            bis.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}