package com.odysii.influx;

import com.odysii.influx.payload.MeasurementType;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.junit.jupiter.api.Assertions;

import javax.security.auth.login.Configuration;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InfluxDBHandler {
    private String host,port,user,pass,dbName;
    private InfluxDB influxDB;
    private String measurement;

    public InfluxDBHandler(String host, String port, String user, String pass,String dbName) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.dbName = dbName;
    }
    public static String defaultRetentionPolicy(String version) {
        if (version.startsWith("0.") ) {
            return "default";
        } else {
            return "autogen";
        }
    }
    public void connect(){
        influxDB = InfluxDBFactory.connect("http://"+host+":"+port, user, pass);
    }
    public void createDB(){
        this.influxDB.createDatabase(dbName);
        //influxDB.createRetentionPolicy("defaultPolicy", dbName, "1d", 1, true);
    }
    //    public void deleteDB(){
//        influxDB.deleteDatabase(dbName);
//    }
    public void createData(){
//        Point point = Point
//                .measurement("cpu")
//                .tag("atag", "test")
//                .addField("idle", 90L)
//                .addField("usertime", 9L)
//                .addField("system", 1L)
//                .build();
//        influxDB.setDatabase(dbName);
//        influxDB.write(point);
//        String dbName = "write_unittest_" + System.currentTimeMillis();
//        this.influxDB.createDatabase(dbName);
//        String rp = defaultRetentionPolicy(this.influxDB.version());
//        this.influxDB.write(dbName, rp, InfluxDB.ConsistencyLevel.ONE, "cpu,atag=test idle=90,usertime=9,system=1");
//        Query query = new Query("SELECT * FROM cpu GROUP BY *", dbName);
//        QueryResult result = this.influxDB.query(query);
//        //Assertions.assertFalse(result.getResults().get(0).getSeries().get(0).getTags().isEmpty());
//        this.influxDB.deleteDatabase(dbName);
    }

    public void producePlayedItems(String measurement,List<Map<String,String>> playedItems) {

        //String dbName = "write_unittest_" + System.currentTimeMillis();
        this.measurement = measurement;
        String rp = defaultRetentionPolicy(this.influxDB.version());
        BatchPoints batchPoints = BatchPoints.database(dbName).retentionPolicy(rp).build();

        Point point = null;
        for (Map<String,String> playedItem : playedItems){
            Point.Builder builder = Point.measurement("S"+this.measurement);
            for (String item : playedItem.keySet()){
                if (item.equals("ChannelId") || item.equals("SiteId")){
                    builder.addField(item,playedItem.get(item));
                }else
                    builder.tag(item,playedItem.get(item));
            }
            point = builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
            batchPoints.point(point);
            //batchPoints.point(point1);
            influxDB.write(batchPoints);
        }
//        Point point1 = Point.measurement("disk").tag("atag", "a").addField("used", 60L).addField("free", 1L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
//        Point point2 = Point.measurement("disk").tag("atag", "b").addField("used", 70L).addField("free", 2L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
//        Point point3 = Point.measurement("disk").tag("atag", "c").addField("used", 80L).addField("free", 3L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
//        Point point4 = Point.measurement("disk").tag("atag", "d").addField("used", 90L).addField("free", 4L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
//        batchPoints.point(point1);
//        batchPoints.point(point2);
//        batchPoints.point(point3);
//        batchPoints.point(point4);
//        this.influxDB.write(batchPoints);

    }
    public void produceEvents(String measurement,Map<String,String> events) {
        this.measurement = measurement;
        String rp = defaultRetentionPolicy(this.influxDB.version());
        BatchPoints batchPoints = BatchPoints.database(dbName).retentionPolicy(rp).build();

        Point point = null;
        Point.Builder builder = Point.measurement(measurement);
        for (String event : events.keySet()){
            if (event.equals("ChannelId") || event.equals("SiteId") || event.equals("Type") || event.equals("TransId")){
                builder.tag(event,events.get(event));
            }else
                builder.addField(event,events.get(event));
        }
        point = builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
        batchPoints.point(point);
        influxDB.write(batchPoints);
    }
    public void produceExample() throws InterruptedException {
        if (this.influxDB.version().startsWith("0.") || this.influxDB.version().startsWith("1.0")) {
            // do not test version 0.13 and 1.0
            return;
        }
        //String dbName = "write_unittest_" + System.currentTimeMillis();
        this.influxDB.createDatabase(dbName);
        String rp = defaultRetentionPolicy(this.influxDB.version());
        BatchPoints batchPoints = BatchPoints.database(dbName).retentionPolicy(rp).build();
        Point point1 = Point.measurement("disk").tag("atag", "a").addField("used", 60L).addField("free", 1L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
        Point point2 = Point.measurement("disk").tag("atag", "b").addField("used", 70L).addField("free", 2L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
        Point point3 = Point.measurement("disk").tag("atag", "c").addField("used", 80L).addField("free", 3L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
        Point point4 = Point.measurement("disk").tag("atag", "d").addField("used", 90L).addField("free", 4L).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).build();
        batchPoints.point(point1);
        batchPoints.point(point2);
        batchPoints.point(point3);
        batchPoints.point(point4);
        this.influxDB.write(batchPoints);

    }
    public void getData(String measurement){
        try {
            Thread.sleep(2000);
            final BlockingQueue<QueryResult> queue = new LinkedBlockingQueue<>();
            Query query = new Query("SELECT * FROM "+measurement, dbName);
            QueryResult queryResult = influxDB.query(query);

            this.influxDB.query(query, 5, queue::add);

            Thread.sleep(2000);
            this.influxDB.deleteDatabase(dbName);

            QueryResult result;
            do {
                result = queue.poll(20, TimeUnit.SECONDS);
                System.out.println(result);
            }while (!"DONE".equals(result.getError()));
        }catch (Exception e){
            e.fillInStackTrace();
        }

        //QueryResult result = queue.poll(20, TimeUnit.SECONDS);
//        Assertions.assertNotNull(result);
//        System.out.println(result);
//        Assertions.assertEquals(2, result.getResults().get(0).getSeries().get(0).getValues().size());
//
//        result = queue.poll(20, TimeUnit.SECONDS);
//        Assertions.assertNotNull(result);
//        System.out.println(result);
//        Assertions.assertEquals(1, result.getResults().get(0).getSeries().get(0).getValues().size());
//
//        result = queue.poll(20, TimeUnit.SECONDS);
//        Assertions.assertNotNull(result);
//        System.out.println(result);
//        Assertions.assertEquals("DONE", result.getError());

//        assertEquals(2, memoryPointList.size());
//        assertTrue(4743696L == memoryPointList.get(0).getFree());
    }
    public void readData(){
        //Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");
    }
}
