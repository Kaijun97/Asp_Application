package com.example.asp_sqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private Button btnSignIn, btnSignUp;
    private EditText etName, etPassword;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String sql = "create table if not exists users (uId integer PRIMARY KEY autoincrement, Name text NOT NULL UNIQUE, Password text)";
        db = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);
        db.execSQL(sql);


        this.btnSignIn=(Button) findViewById(R.id.login);
        this.btnSignUp=(Button) findViewById(R.id.signUp);
        this.etName = (EditText) findViewById(R.id.etUsername);
        this.etPassword = (EditText) findViewById(R.id.etPassword);

        this.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        this.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sqlStatement = "select * from users where Name='"+etName.getText().toString()+"'";
                System.out.println(etName.getText().toString());
                try (Cursor result = db.rawQuery(sqlStatement, null)) {
                    if (result.moveToFirst()) {
                        System.out.println(result.getString(result.getColumnIndex("Name")));
                        if (result.getString(result.getColumnIndex("Name")).equals(etName.getText().toString()) &&
                                result.getString(result.getColumnIndex("Password")).equals(etPassword.getText().toString())) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", etName.getText().toString());
                            db.close();
                            startActivity(intent);
                        } else
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

}
