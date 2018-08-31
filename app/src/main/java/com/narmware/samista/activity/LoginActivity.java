package com.narmware.samista.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.narmware.samista.MyApplication;
import com.narmware.samista.R;
import com.narmware.samista.pojo.Login2;
import com.narmware.samista.support.Constants;
import com.narmware.samista.support.Endpoints;
import com.narmware.samista.support.SharedPreferencesHelper;
import com.narmware.samista.support.SupportFunctions;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    RequestQueue mVolleyRequest;
    @BindView(R.id.edt_uname) protected EditText mUsername;
    @BindView(R.id.edt_pass)protected EditText mPassword;
    @BindView(R.id.btn_login) protected Button mLogin;
    Dialog mNoConnectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mVolleyRequest = Volley.newRequestQueue(LoginActivity.this);
        mNoConnectionDialog = new Dialog(LoginActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        //auto capital edittext
        mUsername.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                String s=arg0.toString();
                if(!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    mUsername.setText(s);
                    mUsername.setSelection(s.length());
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                String s=arg0.toString();
                if(!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    mPassword.setText(s);
                    mPassword.setSelection(s.length());
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areFieldsFilled()) {
                    loginUser(mUsername.getText().toString(), mPassword.getText().toString());
                }
                else {
                    MyApplication.mt("Please fill all details", LoginActivity.this);
                }

            }
        });

    }

    private boolean areFieldsFilled() {
        boolean flag = true;
        if(mUsername.getText().toString().trim() == "" || mUsername.getText().toString().trim().isEmpty()) {
            flag = false;
            mUsername.setError("Cannot be left blank");
        }

        if(mPassword.getText().toString().trim() == "" || mPassword.getText().toString().trim().isEmpty()) {
            flag = false;
            mPassword.setError("Cannot be left blank");
        }
        return flag;
    }
    private void loginUser(String username, String password) {
        HashMap<String,String> param = new HashMap();
        param.put(Endpoints.LOGIN_USERNAME, username);
        param.put(Endpoints.LOGIN_PASSWORD, password);

        String url= SupportFunctions.appendParam(Endpoints.LOGIN_URL,param);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait");
        dialog.setMessage("Validating credentials...");
        if(!dialog.isShowing()) dialog.show();

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {

                            Gson gson = new Gson();
                            Login2 data = gson.fromJson(response.toString(), Login2.class);
                            Log.e("Login Json_string",response.toString());
                                switch (Integer.parseInt(data.getResponse())) {
                                    case Constants.LOGIN_SUCCESS:
                                        SharedPreferencesHelper.setIsLogin(true,LoginActivity.this);
                                        SharedPreferencesHelper.setUserId(data.getUser_id(),LoginActivity.this);

                                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                        break;

                                    case Constants.LOGIN_ACCOUNT_NOT_VERIFIED:
                                        break;

                                    case Constants.LOGIN_INVALID_CREDENTIALS:
                                        MyApplication.mt("Invalid credentials", LoginActivity.this);
                                        break;
                                }

                            if(mNoConnectionDialog.isShowing())
                                mNoConnectionDialog.dismiss();

                                if(dialog.isShowing()) dialog.dismiss();


                        } catch (Exception e) {

                            e.printStackTrace();
                            if(dialog.isShowing()) dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.mt("Server not reachable", LoginActivity.this);
                        showNoConnectionDialog();
                        if(dialog.isShowing()) dialog.dismiss();
                    }
                }
        );
        mVolleyRequest.add(obreq);
    }
    private void showNoConnectionDialog() {
        mNoConnectionDialog.setContentView(R.layout.dialog_no_internet);
        mNoConnectionDialog.setCancelable(false);
        mNoConnectionDialog.show();

        Button tryAgain = mNoConnectionDialog.findViewById(R.id.txt_retry);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(mUsername.getText().toString(), mPassword.getText().toString());
            }
        });
    }

}
