package com.maple.atm;

import android.support.annotation.NonNull;
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
    private EditText edUserid;
    private EditText edPasswd;
    private CheckBox cbRememberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
