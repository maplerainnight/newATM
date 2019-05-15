package com.maple.atm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CAMERA = 5;
    private EditText edUserid;
    private EditText edPasswd;
    private CheckBox cbRememberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA); //看我取得權限了嗎
        if(permission == PackageManager.PERMISSION_GRANTED){
            //takePhoto();
        }else{ //沒有權限 迸出對話框要求權限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
        }
        //存東西到偏好裡
        getSharedPreferences("atm",MODE_PRIVATE)
                .edit()
                .putInt("LEVEL",3)
                .putString("Name","Tom")
                .commit();
        int level = getSharedPreferences("atm",MODE_PRIVATE)
                    .getInt("LEVEL",0);
        Log.d(TAG, "onCreate: " + level);


        edUserid = findViewById(R.id.EdUserid);
        edPasswd = findViewById(R.id.EdPasswd);
        cbRememberID = findViewById(R.id.cb_remember_userid);
        cbRememberID.setChecked(
                getSharedPreferences("atm",MODE_PRIVATE)
                .getBoolean("REMEMBER_ID_OR_NOT",false)
        );
        cbRememberID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("atm",MODE_PRIVATE)
                        .edit()
                        .putBoolean("REMEMBER_ID_OR_NOT",isChecked)
                        .apply();
            }
        });

        String userid = getSharedPreferences("atm",MODE_PRIVATE)
                .getString("USERID","");
        edUserid.setText(userid);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //得到權限 拍照
        startActivity(intent);
    }

    public void login(View view){
        final String userid = edUserid.getText().toString();
        final String passwd = edPasswd.getText().toString();
        FirebaseDatabase.getInstance().getReference("users").child(userid).child("password")
                .addValueEventListener(new ValueEventListener() { //從firebase抓值下來
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String pw = (String)dataSnapshot.getValue();
                        if(pw.equals(passwd)){
                            //記住帳號
                            boolean remember = getSharedPreferences("atm",MODE_PRIVATE)
                                    .getBoolean("REMEMBER_ID_OR_NOT",false);
                            if(remember) {
                                getSharedPreferences("atm", MODE_PRIVATE)
                                        .edit()
                                        .putString("USERID", userid)
                                        .apply();
                            }
                            //回傳正確登入變數
                            setResult(RESULT_OK);
                            finish();
                        }
                        else{
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("登入結果")
                                    .setMessage("登入失敗")
                                    .setPositiveButton("OK",null)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }
    public void quit(View view){

    }
}
