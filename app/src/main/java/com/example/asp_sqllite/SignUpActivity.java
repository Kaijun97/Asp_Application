package com.example.asp_sqllite;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private Button btnBack, btnSignUp;
    private EditText etusername, etpassword, etpasswordConfirm;
    SQLiteDatabase db;
    //private static final String url_Signup = MainActivity.ipBaseAddress+"/SignUpJ.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.btnBack = (Button) findViewById(R.id.btnBack);
        this.btnSignUp = (Button) findViewById(R.id.btnSignUp);
        this.etusername = (EditText) findViewById(R.id.etUsername);
        this.etpassword = (EditText) findViewById(R.id.etPassword);
        this.etpasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);

        db= openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);


        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        this.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etusername.getText().toString();
                String password = etpassword.getText().toString();
                String passwordConfirm = etpasswordConfirm.getText().toString();

                if (username.isEmpty()) {
                    etusername.setError(getResources().getString(R.string.error_field_required));
                } else {
                    if (password.isEmpty() || passwordConfirm.isEmpty())
                        etpassword.setError(getResources().getString(R.string.error_field_required));
                    else {
                        if (password.equals(passwordConfirm)) {
//                            JSONObject dataJson = new JSONObject();
//                            try {
//                                dataJson.put("username", username);
//                                dataJson.put("password", password);
//
//
//                            } catch (JSONException e) {
//
//                            }
//                            postData(url_Signup, dataJson, 1);
                            String sqlStatement = "insert into users (Name ,Password) values( '" + username + "','" + password + "')";
                            String result=updateTable(sqlStatement);
                            if(result.equals("Error updating DB")) {
                                Toast.makeText(getApplicationContext(),"Sign Up unsuccessful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Sign Up Successful",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            etpasswordConfirm.setError(getResources().getString(R.string.error_field_mismatch));
                        }
                    }
                }
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
            return ("Error updating DB");
        }


        Toast.makeText(this, "DB updated", Toast.LENGTH_LONG).show();
        return ("DB updated");

    }
}



//    public void postData(String url,final JSONObject json, final int option){
//    RequestQueue requestQueue = Volley.newRequestQueue(this);
//    JsonObjectRequest json_obj_req = new JsonObjectRequest(
//            Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
//        @Override
//        public void onResponse(JSONObject response) {
//
//
//            switch (option) {
//                case 1:
//                    checkResponseSignUp(response);
//                    break;
//
//            }
//
//        }
//
//    }, new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            error.printStackTrace();
////                String alert_message;
////                alert_message = error.toString();
////                showAlertDialogue("Error", alert_message);
//        }
//
//    });
//    requestQueue.add(json_obj_req);
//}
//    public void checkResponseSignUp (JSONObject response)
//    {
//        Log.i("----Response", response + " " + url_Signup);
//        try {
//            if (response.getInt(TAG_SUCCESS) == 1) {
//
//                finish();
//                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
//                startActivity(i);
//
//
//            } else {
//                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//        }
//    }

