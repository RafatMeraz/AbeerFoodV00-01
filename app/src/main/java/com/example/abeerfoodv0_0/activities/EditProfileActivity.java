package com.example.abeerfoodv0_0.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.abeerfoodv0_0.model.User;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profileImgIV, backButton;
    private EditText nameET, emailET, phoneET, address1ET, address2ET;
    private Button saveButton;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUser = (User) getIntent().getSerializableExtra("user");

        initialization();

    }

    private void initialization() {
        profileImgIV = findViewById(R.id.editProfileImgIV);
        nameET = findViewById(R.id.editProfileFullNameET);
        emailET = findViewById(R.id.editProfileEmailET);
        phoneET = findViewById(R.id.editProfilePhoneET);
        address1ET = findViewById(R.id.editProfileAddress1ET);
        address2ET = findViewById(R.id.editProfileAddress2ET);
        backButton = findViewById(R.id.editProfileBackButton);
        saveButton = findViewById(R.id.editProfileSaveButton);

        nameET.setText(currentUser.getName());
        emailET.setText(currentUser.getEmail());
        phoneET.setText(currentUser.getPhone());
        address1ET.setText(currentUser.getAddress1());
        if (currentUser.getAddress2()=="null")
            address2ET.setText("");
        else
            address2ET.setText(currentUser.getAddress2());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void updateUser() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.UPDATE_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            Log.e("response : ", response.toString());
                            if (userResponse.getBoolean("error")==false) {
                                DynamicToast.makeSuccess(getApplicationContext(), "Update Successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                DynamicToast.makeError(getApplicationContext(), ""+userResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(currentUser.getId()));
                params.put("name", nameET.getText().toString());
                params.put("phone_number", phoneET.getText().toString());
                params.put("addressl1", address1ET.getText().toString());
                if (!TextUtils.isEmpty(address2ET.getText().toString()))
                    params.put("addressl2", address2ET.getText().toString());
                params.put("email", emailET.getText().toString());
                params.put("image", "default.png");
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
}
