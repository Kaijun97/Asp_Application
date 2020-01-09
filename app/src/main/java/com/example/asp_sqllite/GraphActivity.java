package com.example.asp_sqllite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class GraphActivity extends AppCompatActivity {
    private GraphView graphView;
    SQLiteDatabase db;
    private ListView listView;
    SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
    Date time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        db = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);

        Intent intent = getIntent();
        String recIDStr = intent.getStringExtra("recID");

        int recID = Integer.parseInt(recIDStr);
        this.listView = (ListView) findViewById(R.id.list);

        this.graphView = (GraphView) findViewById(R.id.graphView);



        ArrayList<String> listItem = new ArrayList<String>();
        final ArrayList<String> listID = new ArrayList<>();
        final ArrayList<String> listValue = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listValue);

        listView.setAdapter(adapter);
        String selectQuery = "SELECT  * FROM  recordsData where recID='" + recID + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[0]);
        new DataPoint(0,0);
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if(isValueX)
                    return sdf.format(new Date((long) value));
                else
                    return super.formatLabel(value, isValueX);
            }
        });
        graphView.getGridLabelRenderer().setHumanRounding(false);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);
        if (cursor.moveToFirst()) {
            int x = 0;

            do {

                //Add each field of record to listName, listID,listTel
                listID.add("" + cursor.getInt(cursor.getColumnIndex("recID")));
                listValue.add("" + cursor.getInt(cursor.getColumnIndex("Value"))+"\n" +cursor.getString(cursor.getColumnIndexOrThrow("Time")));
                String DateStr = cursor.getString(cursor.getColumnIndexOrThrow("Time"));
                DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date cdate = df2.parse(DateStr);
                    SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm:ss");
                    String timeStr = timeFormat.format(cdate);
                    System.out.println(timeStr);
                    time = timeFormat.parse(timeStr);
                    series.appendData(new DataPoint(time, cursor.getInt(cursor.getColumnIndex("Value"))),true,500);
                } catch (ParseException e) {
                    e.printStackTrace();
                }



                //series.appendData(new DataPoint(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Time"))), cursor.getInt(cursor.getColumnIndex("Value"))),true,500);
//                listValue.add(cursor.getString(cursor.getColumnIndex("Value")));
                x++;

            } while (cursor.moveToNext());
            db.close();
        }

    }
}







