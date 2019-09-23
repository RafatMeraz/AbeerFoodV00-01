package com.example.abeerfoodv0_0.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.example.abeerfoodv0_0.utils.MySingleton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotET;
    private Button sendEmailButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initialization();
    }

    private void initialization() {
        forgotET = findViewById(R.id.forgotPasswordEmailET);
        sendEmailButton = findViewById(R.id.forgotPasswordSendButton);
        backButton = findViewById(R.id.forgotPasswordBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        forgotET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
//        final ProgressDialog dialog = new ProgressDialog(getApplicationContext());
//        dialog.setTitle("Loading...");
//        dialog.setMessage("Please wait a while");
//        dialog.show();

        final StringRequest request = new StringRequest(Request.Method.POST, Constraints.FORGOT_PASSWORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
//                            dialog.dismiss();
                            Log.e( "onResponse: ", response.toString());
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error")){
                                DynamicToast.makeError(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                DynamicToast.makeSuccess(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            e.getStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", forgotET.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void checkInput(){
        if (!TextUtils.isEmpty(forgotET.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(forgotET.getText().toString()).matches()) {
            sendEmailButton.setEnabled(true);
            sendEmailButton.setTextColor(getResources().getColor(R.color.white));
        }else {
            forgotET.setError("Enter a valid email address");
            sendEmailButton.setEnabled(false);
        }

    }
}
