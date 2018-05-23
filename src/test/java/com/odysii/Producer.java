package com.odysii;

import com.odysii.influx.InfluxDBHandler;
import com.odysii.influx.PayloadHandler;

import javax.swing.plaf.metal.MetalBorders;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Producer {
    public static void main(String[]args){

        readCsv();
//        InfluxDBHandler influxDBHandler = new InfluxDBHandler("10.28.76.120","8086","root","root","yossi");
//        influxDBHandler.connect();
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

                if (counter > 1){
                    // use comma as separator
                    String[] customerData = line.split(cvsSplitBy);
                    PayloadHandler payloadHandler = new PayloadHandler(customerData[10]);
                    List<Map<String,String>> payloads = payloadHandler.getPlayedItems();
                    for (Map<String,String> payload : payloads){
                        for (String s : payload.keySet()){
                            System.out.println("Key: "+s +" Value: "+ payload.get(s));
                        }
                    }
                    //System.out.println(counter+"---> Country [code= " + customerData[4] + " , name=" + customerData[5] + "]");
                }
                counter++;
            }

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
