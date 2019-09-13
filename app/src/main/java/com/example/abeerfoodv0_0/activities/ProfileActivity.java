package com.example.abeerfoodv0_0.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.database.SharedPrefManager;
import com.example.abeerfoodv0_0.model.User;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private CircleImageView userImageIV;
    private TextView userFullNameTV, emailTV, phoneTV, address1TV, address2TV;
    private Button editButton, logOutButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialization();
    }

    private void initialization() {
        backButton = findViewById(R.id.profileBackButton);
        userFullNameTV = findViewById(R.id.profileFullName);
        userImageIV = findViewById(R.id.profileImgView);
        emailTV = findViewById(R.id.profileEmailTV);
        phoneTV = findViewById(R.id.profilePhoneTV);
        address1TV = findViewById(R.id.profileAddress1TV);
        logOutButton = findViewById(R.id.profileLogOutButton);
        address2TV = findViewById(R.id.profileAddress2TV);
        editButton = findViewById(R.id.profileEditButton);
        loadUserDetails(Constraints.currentUser.getId());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                finish();
            }
        });
    }

    private void loadUserDetails(final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.USER_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            Log.e("response : ", response.toString());

                            user= new User(
                                    id,
                                    userResponse.getString("email"),
                                    userResponse.getString("name"),
                                    userResponse.getString("addressl1"),
                                    userResponse.getString("addressl2"),
                                    userResponse.getString("phone_number"),
                                    userResponse.getString("image")
                            );
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(Constraints.currentUser.getId(), user.getName(), user.getEmail());
                            Constraints.currentUser = new User(
                                    SharedPrefManager.getInstance(getApplicationContext()).getUserID(),
                                    SharedPrefManager.getInstance(getApplicationContext()).getUserEmail(),
                                    SharedPrefManager.getInstance(getApplicationContext()).getUserName()
                            );
                            userFullNameTV.setText(user.getName());
                            emailTV.setText(user.getEmail());
                            phoneTV.setText(user.getPhone());
                            address1TV.setText(user.getAddress1());
                            if (user.getAddress2()=="null")
                                address2TV.setTextColor(Color.parseColor("#ff0000"));
                            else
                                address2TV.setText(user.getAddress2());

//                            if ((user.getImg()=="default.png)") || (user.getImg()=="null")){
//                                userImageIV.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.user_img));
//                            } else {
//                                Picasso.with(getApplicationContext()).load(user.getImg()).into(userImageIV);
//                            }
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
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails(Constraints.currentUser.getId());
    }
}
