package com.odysii;

import com.odysii.influx.InfluxDBHandler;
import com.odysii.influx.JsonHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class Producer {
    private final static String JSON_DIR = "\\\\orion\\Public\\SysQA\\Odysii\\Odysii Installers\\Solutions\\C-Store 4\\QA\\influx\\";
    static InfluxDBHandler influxDBHandler = new InfluxDBHandler("10.28.76.120","8086","root","root","TestData");
    public static void main(String[] args) throws Exception {
        influxDBHandler.connect();
        influxDBHandler.createDB();
        HttpServer server = HttpServer.create(new InetSocketAddress(1818), 0);
        server.createContext("/influx/producer", new MyHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            String uri = t.getRequestURI().toString();
            String fileName = JSON_DIR+uri.split("=")[1]+".json";
            //String fileName = "\\\\orion\\Public\\SysQA\\Odysii\\Odysii Installers\\Solutions\\C-Store 4\\QA\\influx\\ProjectId_553_chunk_0_create_at_2018-06-17.json";
            String measurement = "events_"+fileName.split("_")[1];
            String content = null;
            //String measurement = ProjectId_123456_chunk_0_create_at_2018-06-17
            try {
                content = new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonHandler jsonHandler = new JsonHandler(content);
            List<Map<String,String>> events = jsonHandler.getEvents();
            for (Map<String,String> event : events){
                influxDBHandler.produceEvents(measurement,event);
            }
            influxDBHandler.getData(measurement);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
