package com.example.abeerfoodv0_0.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
                            mProgressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            if (error==true) {
                                DynamicToast.makeError(getApplicationContext(), ""+ jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (error==false){
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(jsonObject.getJSONObject("id").getInt("id"), fullName, email);
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

    private void checkInputs() {
        if (!TextUtils.isEmpty(fullNameET.getText().toString())){
            if (!TextUtils.isEmpty(emailET.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
                if (!TextUtils.isEmpty(phoneET.getText().toString())){
                    if (!TextUtils.isEmpty(addressET.getText().toString())){
                        if (!TextUtils.isEmpty(passwordET.getText().toString()) && (passwordET.getText().toString().length() >= 6)) {
                            signUpButton.setEnabled(true);
                            signUpButton.setTextColor(getApplication().getResources().getColor(android.R.color.white));
                        } else {
                            signUpButton.setEnabled(false);
                            passwordET.setError("Enter your password more than 6 characters.");
                            signUpButton.setTextColor(getApplication().getResources().getColor(R.color.dark_white));
                        }
                    }else {
                        signUpButton.setEnabled(false);
                        addressET.setError("Enter your password more than 6 characters.");
                        signUpButton.setTextColor(getApplication().getResources().getColor(R.color.dark_white));
                    }
                } else {
                    signUpButton.setEnabled(false);
                    phoneET.setError("Enter your password more than 6 characters.");
                    signUpButton.setTextColor(getApplication().getResources().getColor(R.color.dark_white));
                }
            } else {
                signUpButton.setEnabled(false);
                emailET.setError("Enter your valid email address.");
                signUpButton.setTextColor(getApplication().getResources().getColor(R.color.dark_white));
            }
        } else {
            signUpButton.setEnabled(false);
            fullNameET.setError("Enter your name");
            signUpButton.setTextColor(getApplication().getResources().getColor(R.color.dark_white));
        }
    }

}
