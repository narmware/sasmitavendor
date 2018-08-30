package com.narmware.samista.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.narmware.samista.MyApplication;
import com.narmware.samista.R;
import com.narmware.samista.adapter.CommentAdapter;
import com.narmware.samista.adapter.MyAdapter;
import com.narmware.samista.pojo.Comments;
import com.narmware.samista.pojo.CommentsResponse;
import com.narmware.samista.pojo.DataResponse;
import com.narmware.samista.pojo.Login2;
import com.narmware.samista.support.Constants;
import com.narmware.samista.support.Endpoints;
import com.narmware.samista.support.SharedPreferencesHelper;
import com.narmware.samista.support.SupportFunctions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentActivity extends AppCompatActivity {
    @BindView(R.id.recycler_comment) RecyclerView mRecyclerComment;
    @BindView(R.id.sendButton) ImageButton mImgBtnSend;
    @BindView(R.id.edtMessageArea) EditText mEdtComment;

    ArrayList<Comments> comments;
    MyAdapter commentAdapter;

    RequestQueue mVolleyRequest;
    Dialog mNoConnectionDialog;
    String comment;
    String lead_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        lead_id=intent.getStringExtra(Endpoints.LEAD_ID);

        init();
    }

    private void init() {
        mVolleyRequest = Volley.newRequestQueue(CommentActivity.this);
        mNoConnectionDialog = new Dialog(CommentActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        setCommentAdapter(new LinearLayoutManager(CommentActivity.this));
        GetComments();

        mImgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comment=mEdtComment.getText().toString().trim();
                if(comment!=null) {
                    AddComment();
                    mEdtComment.setText("");
                }
            }
        });
    }

    public void setCommentAdapter(LinearLayoutManager mLayoutManager) {
        comments = new ArrayList<>();
        SnapHelper snapHelper = new LinearSnapHelper();

        mLayoutManager.setReverseLayout(true);
        commentAdapter = new MyAdapter(CommentActivity.this, comments);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(GalleryActivity.this,2);
        mRecyclerComment.setLayoutManager(mLayoutManager);
        mRecyclerComment.setItemAnimator(new DefaultItemAnimator());
        //snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerComment.setAdapter(commentAdapter);
        mRecyclerComment.setNestedScrollingEnabled(false);
        mRecyclerComment.setFocusable(false);

        commentAdapter.notifyDataSetChanged();
    }

    private void GetComments() {
        HashMap<String,String> param = new HashMap();
        param.put(Endpoints.LEAD_ID, lead_id);

        String url= SupportFunctions.appendParam(Endpoints.GET_COMMENTS,param);
       /* final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Getting Comments");
        if(!dialog.isShowing()) dialog.show();*/

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {
                            //Log.e("Comment json",response.toString());
                            Gson gson = new Gson();
                            CommentsResponse commentsResponse = gson.fromJson(response.toString(), CommentsResponse.class);
                            Comments[] comment=commentsResponse.getData();
                            comments.clear();

                            for (Comments item:comment)
                            {
                                comments.add(item);
                            }
                            commentAdapter.notifyDataSetChanged();

                            if(mNoConnectionDialog.isShowing())
                                mNoConnectionDialog.dismiss();

                            //if(dialog.isShowing()) dialog.dismiss();


                        } catch (Exception e) {

                            e.printStackTrace();
                            //if(dialog.isShowing()) dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.mt("Server not reachable", CommentActivity.this);
                        showNoConnectionDialog();
                        //if(dialog.isShowing()) dialog.dismiss();
                    }
                }
        );
        mVolleyRequest.add(obreq);
    }

    private void AddComment() {
        HashMap<String,String> param = new HashMap();
        param.put(Endpoints.USER_ID, SharedPreferencesHelper.getUserId(CommentActivity.this));
        param.put(Endpoints.LEAD_ID, lead_id);
        param.put(Endpoints.COMMENT,comment);

        String url= SupportFunctions.appendParam(Endpoints.ADD_COMMENTS,param);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {
                            //Log.e("AddComment json",response.toString());
                            Gson gson = new Gson();
                            DataResponse dataResponse = gson.fromJson(response.toString(), DataResponse.class);

                            if(dataResponse.getResponse().equals("100"))
                            {
                                GetComments();
                            }

                            if(mNoConnectionDialog.isShowing())
                                mNoConnectionDialog.dismiss();


                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.mt("Can't send message,No internet", CommentActivity.this);
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
                GetComments();
            }
        });
    }
}
