package com.example.asp_sqllite;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private TextView txtName;
    private Button btnPlay, btnShowData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String sql = "create table if not exists records (recId integer PRIMARY KEY autoincrement, Name text, Value text, Time DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (Name)\n" +
//                "REFERENCES users(Name))";
        String sql = "create table if not exists records (recId integer PRIMARY KEY autoincrement, Name text, FOREIGN KEY (Name)\n" +
                "REFERENCES users(Name))";
        db = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);
        db.execSQL(sql);

        sql="create table if not exists recordsData (recDataID integer PRIMARY KEY autoincrement, recID integer, Value text, Time DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (recID)\n" +
                "REFERENCES records(recID))";
        db = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);
        db.execSQL(sql);
        db.close();
        this.txtName= (TextView) findViewById(R.id.txtName);
        this.btnPlay=(Button)findViewById(R.id.btnPlay);
        this.btnShowData=(Button) findViewById(R.id.btnShowData);
        final Intent intent=getIntent();
        this.txtName.setText(intent.getStringExtra("username"));

        this.btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent recordActivity = new Intent(MainActivity.this, AllRecordsActivity.class);
                Intent recordActivity = new Intent(MainActivity.this, AllRecordsActivity.class);
                recordActivity.putExtra("username",intent.getStringExtra("username"));
                startActivity(recordActivity);

            }
        });

        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent play = new Intent(MainActivity.this, PlayActivity.class);
                play.putExtra("username",intent.getStringExtra("username"));
                startActivity(play);

            }
        });
    }

    String updateTable(String sql) {

        try {

            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {

            System.out.println(e.toString());
            return ("Error updating DB");
        }


        Toast.makeText(this, "DB updated", Toast.LENGTH_LONG).show();
        return ("DB updated");

    }
}
