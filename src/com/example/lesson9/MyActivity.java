package com.example.lesson9;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyActivity extends Activity {
    public static String refreshMsg;
    public static TextView cityName;
    private static Button list;
    private static Button load;
    private static ListView weather;
    public static MyAdapter adapter;
    public static ArrayList<ItemWeather> itemList = new ArrayList<ItemWeather>();
    public static ArrayList<City> cities = new ArrayList<City>();
    public static int select = 0;
    public static MyActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        instance = this;
        refreshMsg = getString(R.string.refreshmsg);

        cities.add(new City("Moscow, Russia","55.752","37.616"));
        cities.add(new City("Saint Petersburg, Russia","59.894","30.264"));
        cities.add(new City("London, United Kingdom","51.517","-0.106"));

        cityName = (TextView) findViewById(R.id.cityName);
        list = (Button) findViewById(R.id.list);
        load = (Button) findViewById(R.id.load);
        weather = (ListView) findViewById(R.id.listweather);
        adapter = new MyAdapter(this,itemList);
        weather.setAdapter(adapter);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CityList.class);
                startActivity(intent);
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeather();
            }
        });
    }

    public void loadWeather(){
        itemList.clear();
        adapter.notifyDataSetChanged();
        Intent intent = new Intent(this,MyIntentServ.class);
        startService(intent.putExtra("task","load").putExtra("lat", cities.get(select).latitude).putExtra("lon",cities.get(select).longitude).putExtra("date",0));
        startService(intent.putExtra("task","load").putExtra("lat", cities.get(select).latitude).putExtra("lon",cities.get(select).longitude).putExtra("date",1));
        startService(intent.putExtra("task","load").putExtra("lat", cities.get(select).latitude).putExtra("lon",cities.get(select).longitude).putExtra("date",2));
    }
}
