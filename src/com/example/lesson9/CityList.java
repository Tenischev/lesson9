package com.example.lesson9;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 01.03.14
 * Time: 3:47
 * To change this template use File | Settings | File Templates.
 */
public class CityList extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_list);

        final ArrayList<String> city = new ArrayList<String>();
        for (int i=0;i<MyActivity.cities.size();i++)
            city.add(MyActivity.cities.get(i).name);

        ListView listView = (ListView) findViewById(R.id.listcity);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.label,city);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int index, long arg3) {
                MyActivity.cityName.setText(city.get(index));
                MyActivity.select = index;
                MyActivity.instance.loadWeather();
                close();
            }
        });
    }

    private void close(){
        super.onBackPressed();
    }
}
