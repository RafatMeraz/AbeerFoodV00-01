package com.example.abeerfoodv0_0.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.example.abeerfoodv0_0.utils.RequestHandler;
import com.example.abeerfoodv0_0.database.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailET, passwordET, addressET, phoneET, fullNameET;
    private TextView signInTV;
    private Button signUpButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
//        if (!Constraints.isConnectedToInternet(this)){
//            finish();
//            startActivity(new Intent(this, NetConnectionFailedActivity.class));
//        }
        initialization();
    }

    private void initialization() {
        emailET = findViewById(R.id.signInEmailET);
        passwordET = findViewById(R.id.signInPasswordET);
        addressET = findViewById(R.id.addressET);
        phoneET = findViewById(R.id.phoneET);
        fullNameET = findViewById(R.id.fullnameET);
        signInTV = findViewById(R.id.signInActButton);
        signUpButton = findViewById(R.id.signInButton);

        mProgressDialog = new ProgressDialog(this);

        signInTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });
    }

    private void createNewUser() {
        final String fullName = fullNameET.getText().toString();
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();
        final String phone = phoneET.getText().toString();
        final String address = addressET.getText().toString();

        mProgressDialog.setMessage("Please wait a while...");
        mProgressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constraints.USER_REG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            if (error==true) {
                                Toast.makeText(getApplicationContext(), "Error : " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (error==false){
                                Toast.makeText(getApplicationContext(), "" + jsonObject.getJSONObject("id").getInt("id"), Toast.LENGTH_SHORT).show();
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(jsonObject.getJSONObject("id").getInt("id"), fullName, email);
                                Log.e("ERROR : ",   SharedPrefManager.getInstance(getApplicationContext()).getUserID()+
                                        SharedPrefManager.getInstance(getApplicationContext()).getUserEmail()+
                                        SharedPrefManager.getInstance(getApplicationContext()).getUserName());

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.hide();
                        Toast.makeText(getApplicationContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("name", fullName);
                params.put("email", email);
                params.put("password", password);
                params.put("address1", address);
                params.put("phone_number", phone);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
