package com.example.lesson9;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 01.03.14
 * Time: 4:13
 * To change this template use File | Settings | File Templates.
 */
public class MyIntentServ extends IntentService {

    private String link = "http://api.worldweatheronline.com/free/v1/weather.ashx?key=8medbha55pyuzwusucmad53b&q=%s,%s&cc=no&date=%s&format=xml";

    public MyIntentServ() {
        this("MyIntentServ");
    }

    public MyIntentServ(String name) {
        super(name);
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent){
        String task = intent.getStringExtra("task");
        if ("load".equals(task)){
            String lat = intent.getStringExtra("lat");
            String lon = intent.getStringExtra("lon");
            int inc = intent.getIntExtra("date", 0);
            try {
                Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * inc);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                URL url = new URL(String.format(link,lat,lon,simpleDateFormat.format(date)));
                String var6 = "";
                URLConnection connection;
                connection = url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                String encode = "utf-8";
                Scanner scanner = new Scanner(connection.getInputStream(),encode);
                while (scanner.hasNext()) {
                    var6 += scanner.nextLine();
                }
                MySAXApp mySAXApp = new MySAXApp();
                mySAXApp.parseSAX(var6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MySAXApp extends DefaultHandler {
        private String currentElement = null;
        private String date;
        private String wind;
        private String minT;
        private String maxT;
        private String desc;
        private String icon;
        private boolean itemOpen;


        public void parseSAX(String rss) {
            try {
                System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
                XMLReader xr = XMLReaderFactory.createXMLReader();
                MySAXApp handler = new MySAXApp();
                xr.setContentHandler(handler);
                xr.setErrorHandler(handler);
                File tmp = File.createTempFile("123", null);
                FileWriter fw = new FileWriter(tmp);
                BufferedWriter bf = new BufferedWriter(fw);
                bf.write(rss);
                bf.close();
                FileReader r = new FileReader(tmp);
                xr.parse(new InputSource(r));
                tmp.deleteOnExit();
            } catch(Exception e){
                System.out.println(e);
            }
        }

        public void startElement(String uri, String local_name, String raw_name, Attributes amap){
            currentElement = local_name;
            if ("weather".equals(local_name)){
                itemOpen = true;
                wind="";
                maxT="";
                minT="";
                desc="";
                icon="";
                date="";
            }
        }

        public void endElement(String uri, String local_name, String raw_name){
            if ("weather".equals(local_name)){
                itemOpen = false;
                MyActivity.itemList.add(new ItemWeather(date,wind,minT,maxT,desc,download(icon)));

                Intent intent = new Intent("com.example.lesson9.REFRESH");
                MyActivity.instance.sendBroadcast(intent);
            }
        }

        public void characters(char[] ch, int start, int length){
            String value = new String(ch,start,length);
            if (!Character.isISOControl(value.charAt(0))) {
                if (itemOpen){
                    if ("tempMaxC".equals(currentElement)) {
                        maxT += value;
                    } else if ("tempMinC".equals(currentElement)) {
                        minT += value;
                    } else if ("windspeedKmph".equals(currentElement)) {
                        wind += value;
                    } else if ("weatherDesc".equals(currentElement)) {
                        desc += value;
                    } else if ("weatherIconUrl".equals(currentElement)) {
                        icon += value;
                    } else if ("date".equals(currentElement)) {
                        date += value;
                    }
                }
            }
        }

        private Bitmap download(String link){
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
                //return BitmapFactory.decodeStream((InputStream) new URL(link).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
