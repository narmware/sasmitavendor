package com.narmware.samista.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.narmware.samista.R;
import com.narmware.samista.pojo.Lead;
import com.narmware.samista.support.Endpoints;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedLeadActivity extends AppCompatActivity {

    Lead leadItem;
    private int STORAGE_PERMISSION_CODE = 23;
    public static CoordinatorLayout mRootView;

    @BindView(R.id.detail_tv_lead_id) TextView mTxtId;
    @BindView(R.id.detail_tv_trading) TextView mTxtTradingName;
    @BindView(R.id.detail_tv_person) TextView mTxtPersonName;
    @BindView(R.id.detail_tv_contact) TextView mTxtContactNumber;
    @BindView(R.id.detail_tv_pincode) TextView mTxtPincode;
    @BindView(R.id.detail_tv_address) TextView mTxtAddress;

    @BindView(R.id.detail_tv_nature) TextView mTxtNature;
    @BindView(R.id.detail_tv_loan_type) TextView mTxtLoanType;
    @BindView(R.id.detail_tv_since) TextView mTxtSince;
    @BindView(R.id.detail_tv_channel) TextView mTxtChannel;
    @BindView(R.id.detail_bt_close) Button mBtnCloseLead;
    @BindView(R.id.detail_tv_remark) TextView mTxtRemark;
    @BindView(R.id.lead_bottom) LinearLayout mLeadBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_lead);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Lead Details");

        mRootView=findViewById(R.id.main_content);

        Intent intent=getIntent();
        leadItem= (Lead) intent.getSerializableExtra("Lead");

        init();

        statusCheck();

        if (isCallAllowed()) {

            if (ContextCompat.checkSelfPermission(DetailedLeadActivity.this, String.valueOf(new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"})) != 0) {

            } else {

            }
            return;
        }
        requestCallPermission();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chat) {
            Intent intent=new Intent(DetailedLeadActivity.this,CommentActivity.class);
            intent.putExtra(Endpoints.LEAD_ID,leadItem.getId());
            intent.putExtra(Endpoints.STATUS,leadItem.getStatus());
            intent.putExtra(Endpoints.TITLE,leadItem.getTrading_name());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isCallAllowed() {
        if (ContextCompat.checkSelfPermission(DetailedLeadActivity.this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            return true;
        }
        return false;
    }

    private void requestCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailedLeadActivity.this, String.valueOf(new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION","android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"}))) {
            ActivityCompat.requestPermissions(DetailedLeadActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION","android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"}, this.STORAGE_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(DetailedLeadActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION","android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"}, this.STORAGE_PERMISSION_CODE);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != this.STORAGE_PERMISSION_CODE) {
            return;
        }
    }

    private void init() {
        ButterKnife.bind(this);
        if(leadItem!=null)
        {
            mTxtId.setText(leadItem.getId());
            mTxtTradingName.setText(leadItem.getTrading_name());
            mTxtPersonName.setText(leadItem.getContact_name());
            mTxtContactNumber.setText(leadItem.getMobile_number());
            mTxtAddress.setText(leadItem.getBusiness_address());
            mTxtPincode.setText(leadItem.getPincode());
            mTxtNature.setText(leadItem.getNature_of_business());
            mTxtLoanType.setText(leadItem.getLoan_type());
            mTxtSince.setText(leadItem.getDoing_business_since());
            mTxtChannel.setText(leadItem.getChannel_partner());
            mTxtRemark.setText(leadItem.getRemark());

            if(leadItem.getStatus().equals(Endpoints.OPEN))
            {
                mLeadBottom.setVisibility(View.VISIBLE);
            }
            if(leadItem.getStatus().equals(Endpoints.CLOSEDIN)|| leadItem.getStatus().equals(Endpoints.CLOSEDOUT))
            {
                mLeadBottom.setVisibility(View.GONE);
            }

            mBtnCloseLead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(DetailedLeadActivity.this,CameraActivity.class);
                    intent.putExtra(Endpoints.LEAD_ID,leadItem.getId());
                    startActivity(intent);
                }
            });
        }
    }

    public void showSnack()
    {
        Snackbar.make(mRootView, "GPS is disable", Snackbar.LENGTH_INDEFINITE)
                .setAction("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //requestCallPermission();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),10);

                        //turnGPSOn();
                    }
                })
                .setActionTextColor(Color.RED)
                .show();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(DetailedLeadActivity.this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            showSnack();
            mBtnCloseLead.setEnabled(false);

        }else{
            mBtnCloseLead.setEnabled(true);
            //Toast.makeText(DetailedLeadActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("GPS")) { //if gps is disabled
            Toast.makeText(this, "Disable GPS", Toast.LENGTH_SHORT).show();
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }


            @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10)
        {
            statusCheck();
        }
    }
}
