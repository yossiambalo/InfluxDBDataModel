package com.odysii;

import com.odysii.influx.InfluxDBHandler;
import com.odysii.influx.JsonHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class Producer {
    private static final Logger LOGGER = Logger.getLogger(Producer.class);
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
        public void handle(HttpExchange t) {
            String response = "This is the response";
            String uri = t.getRequestURI().toString();
            String measurement = null;
            String content = null;
            try{
            String fileName = JSON_DIR+uri.split("=")[1]+".json";
            measurement = "events_"+fileName.split("_")[1];
                content = new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (Exception e) {
               LOGGER.error("Failed to read file content: "+e.fillInStackTrace());
            }
            JsonHandler jsonHandler = new JsonHandler(content);
            List<Map<String,String>> events = jsonHandler.getEvents();
            for (Map<String,String> event : events){
                influxDBHandler.produceEvents(measurement,event);
            }
            influxDBHandler.getData(measurement);
            OutputStream os = null;
            try
            {
                t.sendResponseHeaders(200, response.length());
                os = t.getResponseBody();
                os.write(response.getBytes());
            }catch (IOException e){
                LOGGER.error(e.fillInStackTrace());
            }finally {
                try {
                    os.close();
                } catch (IOException e) {
                    LOGGER.error(e.fillInStackTrace());
                }
            }
        }
    }
}
