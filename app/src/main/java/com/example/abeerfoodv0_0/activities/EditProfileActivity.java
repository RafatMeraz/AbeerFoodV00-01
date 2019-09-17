package com.example.abeerfoodv0_0.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.model.User;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.example.abeerfoodv0_0.utils.MySingleton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText nameET, emailET, phoneET, address1ET, address2ET;
    private Button saveButton;
    private CircleImageView profileImgIV;
    private User currentUser;
    private final int PICK_IMG_REQUEST = 75;
    private Uri saveUri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        currentUser = (User) getIntent().getSerializableExtra("user");
        initialization();

    }

    private void initialization() {
        profileImgIV = findViewById(R.id.editProfileImgIVSpa);
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
        if (!currentUser.getImg().equals("default.png"))
            Picasso.with(getApplicationContext()).load(Constraints.IMG_BASE_URL+currentUser.getImg()).into(profileImgIV);

        profileImgIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            profileImgIV.setImageURI(null);
            profileImgIV.setImageURI(saveUri);
            uploadImage();
        }
    }

    private void uploadImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.UPLOAD_PROFILE_IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject array = new JSONObject(response);
                            if (array.getBoolean("error")){
                                DynamicToast.makeError(getApplicationContext(), "Profile Image Upload Failed!", Toast.LENGTH_SHORT).show();
                            }else {

                                DynamicToast.makeSuccess(getApplicationContext(), "Profile Image Uploaded!", Toast.LENGTH_SHORT).show();
                            }
                            Log.e( "onResponse: ", array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("user_id", String.valueOf(Constraints.currentUser.getId()));
                params.put("image", imageToString(bitmap));
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private void updateUser() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Updating Profile...");
        dialog.setMessage("Please Wait a while....");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.UPDATE_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
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
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
}
