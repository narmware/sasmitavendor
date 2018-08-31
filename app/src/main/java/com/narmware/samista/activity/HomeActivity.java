package com.narmware.samista.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.narmware.samista.MyApplication;
import com.narmware.samista.R;
import com.narmware.samista.fragment.ClosedLeadFragment;
import com.narmware.samista.fragment.LiveLeadFragment;
import com.narmware.samista.pojo.CheckUserStatus;
import com.narmware.samista.pojo.DataResponse;
import com.narmware.samista.pojo.Login2;
import com.narmware.samista.support.Constants;
import com.narmware.samista.support.Endpoints;
import com.narmware.samista.support.SharedPreferencesHelper;
import com.narmware.samista.support.SupportFunctions;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends AppCompatActivity implements LiveLeadFragment.OnFragmentInteractionListener,ClosedLeadFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    RequestQueue mVolleyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mVolleyRequest = Volley.newRequestQueue(HomeActivity.this);
        checkStatus();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure")
                    .setContentText("Your want to Logout")
                    .setConfirmText("Yes")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            Logout();
                        }
                    })
                    .showCancelButton(true)
                    .setCancelText("Cancel")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment=null;

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if(position==0)
            {
                fragment=new LiveLeadFragment();
            }
            if(position==1)
            {
                fragment=new ClosedLeadFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    private void checkStatus() {
        HashMap<String,String> param = new HashMap();
        param.put(Endpoints.USER_ID, SharedPreferencesHelper.getUserId(HomeActivity.this));

        String url= SupportFunctions.appendParam(Endpoints.CHECK_USER_STATUS,param);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {

                            //Log.e("Status Json_string",response.toString());
                            Gson gson = new Gson();
                            CheckUserStatus checkUserStatus = gson.fromJson(response.toString(), CheckUserStatus.class);
                            if(checkUserStatus.getUser_status().equals(Endpoints.DEACTIVE))
                            {
                                SharedPreferencesHelper.setIsLogin(false,HomeActivity.this);
                                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.mt("Server not reachable", HomeActivity.this);

                    }
                }
        );
        mVolleyRequest.add(obreq);
    }

    private void Logout() {
        HashMap<String,String> param = new HashMap();
        param.put(Endpoints.USER_ID, SharedPreferencesHelper.getUserId(HomeActivity.this));

        String url= SupportFunctions.appendParam(Endpoints.LOGOUT_URL,param);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {

                            Log.e("Logout Json_string",response.toString());
                            Gson gson = new Gson();
                            DataResponse checkUserStatus = gson.fromJson(response.toString(), DataResponse.class);
                            if(checkUserStatus.getResponse().equals("100"))
                            {
                                SharedPreferencesHelper.setIsLogin(false,HomeActivity.this);
                                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.mt("Server not reachable", HomeActivity.this);

                    }
                }
        );
        mVolleyRequest.add(obreq);
    }

}
