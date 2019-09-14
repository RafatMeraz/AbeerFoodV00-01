package com.example.abeerfoodv0_0.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SignInActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private ProgressDialog mProgressDialog;
    private Button signInButton;
    private TextView forgotPassTV;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//        if (!Constraints.isConnectedToInternet(this)){
//            finish();
//            startActivity(new Intent(this, NetConnectionFailedActivity.class));
//        }
        if (SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }



        initialization();
    }

    private void initialization() {
        emailET = findViewById(R.id.signInEmailET);
        passwordET = findViewById(R.id.signInPasswordET);
        signInButton = findViewById(R.id.signInButton);
        forgotPassTV = findViewById(R.id.forgotPassET);
        backButton = findViewById(R.id.backButton);
        mProgressDialog = new ProgressDialog(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        mProgressDialog.setMessage("Please wait for a while...");
        mProgressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constraints.USER_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mProgressDialog.hide();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), "Error : " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getBoolean("error")==false){
                                String userName = jsonObject.getString("name");
                                String email = jsonObject.getString("email");
                                int id = jsonObject.getInt("id");
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(id, userName, email);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                Toast.makeText(getApplicationContext(), "Response error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
