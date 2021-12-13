package com.mee.msjgrp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.PipedReader;
import java.lang.ref.Reference;

public class yenihesapvar extends AppCompatActivity {

    private Toolbar actionbarregister;
    private EditText txtUsername,txtmail,txtpassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference kokreference;


    public void init(){

        actionbarregister =(Toolbar) findViewById(R.id.actionbarregister);
        setSupportActionBar(actionbarregister);
        getSupportActionBar().setTitle("HESAP OLUŞTUR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

          txtUsername = (EditText) findViewById(R.id.txtusername);
          txtmail = (EditText) findViewById(R.id.txtemail);
          txtpassword = (EditText) findViewById(R.id.txtpassword);
          btnRegister = (Button) findViewById(R.id.btnregister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createNewAccount();
            }

        });

    }

    private void createNewAccount() {

        String username = txtUsername.getText().toString();
        String email = txtmail.getText().toString();
        String password = txtpassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Email Alanı Boş Olamaz !",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Şifre Alanı Boş Olamaz !",Toast.LENGTH_LONG).show();
        }else{
             auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {

                     if (task.isSuccessful()){

                         String mevcutkullanıcılar=auth.getCurrentUser().getUid();
                         kokreference.child("kullanicilar").child(mevcutkullanıcılar).setValue("");

                         Toast.makeText(yenihesapvar.this,"Hesap oluşturuldu", Toast.LENGTH_LONG).show();
                         Intent loginIntent = new Intent(yenihesapvar.this,hesapvar.class);
                         startActivity(loginIntent);
                         finish();


                     }else{
                         Toast.makeText(yenihesapvar.this,"Bir hata oluştu",Toast.LENGTH_LONG).show();
                     }
                 }
             });
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yenihesapvar);
        kokreference=FirebaseDatabase.getInstance().getReference();
        init();

    }
}
