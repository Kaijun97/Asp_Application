package com.example.asp_sqllite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class AllRecordsActivity extends AppCompatActivity {
    ListView listView;
    Button btnBack;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records);
        Intent intent=getIntent();
        String username = intent.getStringExtra("username");

        db= openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);

        this.listView=(ListView) findViewById(R.id.list);
        this.btnBack=(Button) findViewById(R.id.btnBack);

        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<String> listItem= new ArrayList<String>();
        final ArrayList<String> listID = new ArrayList<>();
        final ArrayList<String> listUsername = new ArrayList<>();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,listUsername);

        listView.setAdapter(adapter);

        String selectQuery = "SELECT  * FROM  records where Name='"+username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {

            do {

                //Add each field of record to listName, listID,listTel

                listUsername.add(cursor.getString(cursor.getColumnIndex("Name")));
                listID.add(""+cursor.getInt(cursor.getColumnIndex("recId")));
//                listValue.add(cursor.getString(cursor.getColumnIndex("Value")));

            } while (cursor.moveToNext());

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long id) {
                int recordPosition = postion;

                String name=(String) listView.getItemAtPosition(recordPosition);
                String recID = (String) listID.get(recordPosition).toString();
//                String value=(String) listValue.get(recordPosition).toString();

                Toast.makeText(getApplicationContext(), recordPosition + " ID: " +recID+ " Name: "+ name, Toast.LENGTH_LONG ).show();

                Intent intent= new Intent(AllRecordsActivity.this, GraphActivity.class);
                intent.putExtra("Name",name);
                intent.putExtra("recID",recID.toString());
                //intent.putExtra("value", value);
                startActivity(intent);
            }
        });


        // close db connection

        db.close();

    }

}
