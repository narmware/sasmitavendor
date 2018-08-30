package com.narmware.samista.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

public class CameraActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate{

    String imagePath,lead_id;
    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();
    @BindView(R.id.img_lead) ImageView mImgLead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        lead_id=intent.getStringExtra(Endpoints.LEAD_ID);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);startActivityForResult(cameraIntent, 100);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
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
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.e("Image path",picturePath);
            uploadMultipart(picturePath);

         /*   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("Bitmap img",encoded);*/

            //uploadMultipart(finalFile.toString());
            mImgLead.setImageBitmap(photo);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
         imagePath = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
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
            new MultipartUploadRequest(CameraActivity.this,uploadId,Endpoints.SEND_SELFIE)
                    .addFileToUpload(path, Endpoints.CLOSED_LEAD_IMG) //Adding file
                    .addParameter(Endpoints.LEAD_ID, lead_id)//Adding text parameter to the request
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
