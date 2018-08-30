package com.narmware.samista.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.narmware.samista.R;
import com.narmware.samista.broadcast.SingleUploadBroadcastReceiver;
import com.narmware.samista.support.Endpoints;
import com.narmware.samista.support.SharedPreferencesHelper;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CameraActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate{

    String lead_id,picturePath;
    String lat,longi;

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();
    @BindView(R.id.img_lead) ImageView mImgLead;
    @BindView(R.id.img_placeholder) ImageView mImgPlaceholder;
    @BindView(R.id.btn_take_photo) Button mBtnTakePhoto;
    @BindView(R.id.btn_confirm) Button mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        lead_id=intent.getStringExtra(Endpoints.LEAD_ID);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);startActivityForResult(cameraIntent, 100);

        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);startActivityForResult(cameraIntent, 100);
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                uploadMultipart(picturePath);
            }
        });
    }

    public void getLocation()
    {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        lat= String.valueOf(latitude);
        longi= String.valueOf(longitude);

        Log.e("Current location",lat+"  "+longi);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            mImgPlaceholder.setVisibility(View.INVISIBLE);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.e("result data",data.getExtras().get("data").toString());

            Uri tempUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(tempUri));
            System.out.println(finalFile);

            String[] filePathColumn = { MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(tempUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.e("Image path",picturePath);

         /*   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("Bitmap img",encoded);*/

            mImgLead.setImageBitmap(photo);
        }
        else{
            mImgPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String imagePath = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(imagePath);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void uploadMultipart(String path) {

        String uploadId = UUID.randomUUID().toString();

        uploadReceiver.setDelegate(this);
        uploadReceiver.setUploadID(uploadId);

        //Uploading code
        try {
            //Creating a multi part request
            new MultipartUploadRequest(CameraActivity.this,uploadId,Endpoints.CONFIRM_LEAD)
                    .addFileToUpload(path, Endpoints.CLOSED_LEAD_IMG) //Adding file
                    .addParameter(Endpoints.LEAD_ID, lead_id)//Adding text parameter to the request
                    .addParameter(Endpoints.LATITUDE, lat)
                    .addParameter(Endpoints.LONGITUDE,longi)
                    .setMaxRetries(2)
                    //.setNotificationConfig(new UploadNotificationConfig())
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(CameraActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    @Override
    public void onProgress(int progress) {
       /* dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Uploading...");
        dialog.setCancelable(false);
        dialog.show();*/
        Log.e("Progress",""+progress);
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        Log.e("ServerProgress",uploadedBytes+" ");
    }

    @Override
    public void onError(Exception exception) {
        Log.e("ServerError","Errrrrorrrr!!!!");
        Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        //dialog.dismiss();
        Log.e("ServerResponse", new String(serverResponseBody) + "   " + serverResponseCode);
        Gson gson=new Gson();

        SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(CameraActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Lead closed successfully !")
                //.setContentText("Your want to Logout")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();
                        Intent i = new Intent(CameraActivity.this, HomeActivity.class);
                        // set the new task and clear flags
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        /* ImageUploadResponse imageUploadResponse=gson.fromJson(new String(serverResponseBody),ImageUploadResponse.class);
        SharedPreferencesHelper.setUserProfileImage(imageUploadResponse.getUrl(),MainActivity.this);
        Picasso.with(MainActivity.this)
                .load(imageUploadResponse.getUrl())
                .placeholder(R.drawable.placeholder)
                .into(ProfileFragment.mImgProf);*/
    }

    @Override
    public void onCancelled() {
    }
}
