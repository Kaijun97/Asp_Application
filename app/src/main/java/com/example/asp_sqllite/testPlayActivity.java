package com.example.asp_sqllite;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class testPlayActivity extends AppCompatActivity {
    Button btnTest;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_play);

        db= openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);

        btnTest =  (Button) findViewById(R.id.button);
        Intent intent= getIntent();
        String recIDStr=intent.getStringExtra("ID");
        final int recID= Integer.parseInt(recIDStr);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i =0; i<100;i++) {
                    final int random = new Random().nextInt(61) + 20;
                    final String randomStr= Integer.toString(random);
                    String sql = "insert into recordsData(recID, Value) values('" + recID + "', '" + random + "' )";
                    //System.out.println(random);
                    String result = updateTable(sql);
                    System.out.println(result);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        });
    }

    private String updateTable(String sql) {

        try {

            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {

            System.out.println(e.toString());
            return ("Error");
        }


        Toast.makeText(this, "DB updated", Toast.LENGTH_LONG).show();
        return ("Welcome");

    }
}
