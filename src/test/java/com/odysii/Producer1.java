package com.odysii;

import com.odysii.influx.InfluxDBHandler;
import com.odysii.influx.JsonHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Producer1 {
    static InfluxDBHandler influxDBHandler;
    public static void main(String[]args){
        influxDBHandler = new InfluxDBHandler("10.28.76.120","8086","root","root","TestData");
        influxDBHandler.connect();
        influxDBHandler.createDB();
        String filename = "C:\\yossi\\documents\\projectiD_1234_create.json";
        String content = null;
        String measurement = "events_"+filename.split("_")[1];
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonHandler jsonHandler = new JsonHandler(content);
        List<Map<String,String>> events = jsonHandler.getEvents();
        for (Map<String,String> event : events){
            influxDBHandler.produceEvents(measurement,event);
        }

        influxDBHandler.getData(measurement);
//        readCsv();
        //influxDBHandler.deleteDB();
//        try {
//            influxDBHandler.produce();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            influxDBHandler.getData();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        influxDBHandler.createDB();
//        influxDBHandler.createData();
//        influxDBHandler.getData();
    }

    private static void readCsv(){
        String csvFile = "C:\\yossi\\documents\\Trans_cg1004_r.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            int counter = 0;
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                if (counter >= 1){
                    // use comma as separator
                    String[] customerData = line.split(cvsSplitBy);
                    Map<String,String> additionalData = new HashMap<>();
                    additionalData.put("Id",customerData[0]);
                    additionalData.put("ChannelId",customerData[1]);
                    additionalData.put("SiteId",customerData[2]);
                    //additionalData.put("ProjectId",customerData[3]);
                    additionalData.put("PosId",customerData[4]);
                    additionalData.put("TransactionGuid",customerData[5]);
                    additionalData.put("TransactionTime",customerData[6]);
                    additionalData.put("TransactionDate",customerData[7]);
                    additionalData.put("ProcessStatus",customerData[8]);
                    JsonHandler jsonHandler = new JsonHandler(customerData[10],additionalData);
                    List<Map<String,String>> playedItems = jsonHandler.getPlayedItems();
                    influxDBHandler.producePlayedItems(customerData[3],playedItems);
//                    for (Map<String,String> playedItem : playedItems){
//                        for (String s : playedItem.keySet()){
//                            System.out.println("Key: "+s +" Value: "+ playedItem.get(s));
//                        }
//                    }
                    //System.out.println(counter+"---> Country [code= " + customerData[4] + " , name=" + customerData[5] + "]");
                }
                counter++;
            }
            //influxDBHandler.getData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
