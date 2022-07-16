package com.mee.msjgrp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class hesapvar extends AppCompatActivity {

    private Toolbar actionbarlogin;
    private EditText txtmail,txtpassword;
    private Button btnlogin,telefonbtn;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    Strings metin = new Strings();

    public void init(){

        actionbarlogin=(Toolbar) findViewById(R.id.actionbarlogin);
        setSupportActionBar(actionbarlogin);
        getSupportActionBar().setTitle(metin.p);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth =FirebaseAuth.getInstance();
        currentUser =auth.getCurrentUser();

        txtmail = (EditText) findViewById(R.id.txtemaillogin);
        txtpassword = (EditText) findViewById(R.id.txtpasswordlogin);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        telefonbtn=(Button) findViewById(R.id.btntelefon);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hesapvar);
        init();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        telefonbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telefonouturumu=new Intent(hesapvar.this,TelefonoturumActivity.class);
                startActivity(telefonouturumu);
            }
        });
    }
    private void loginUser() {

      String email = txtmail.getText().toString();
      String password = txtpassword.getText().toString();

      if (TextUtils.isEmpty(email)){
          Toast.makeText(this,metin.q,Toast.LENGTH_LONG).show();
      }else if (TextUtils.isEmpty(password)){
          Toast.makeText(this,metin.r, Toast.LENGTH_LONG).show();
      }else{
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

               if (task.isSuccessful()){
                   Toast.makeText(hesapvar.this,metin.t,Toast.LENGTH_LONG).show();
                   Intent mainIntent = new Intent(hesapvar.this,MainActivity2.class);
                   startActivity(mainIntent);
                   finish();
               }else {
                   Toast.makeText(hesapvar.this,metin.u,Toast.LENGTH_LONG).show();
               }
                }
            });
      }
    }
}