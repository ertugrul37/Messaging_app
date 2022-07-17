package com.mee.msjgrp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class TelefonoturumActivity extends AppCompatActivity {
    Strings metin = new Strings();
    private Button dogrulamakodugonderbtn,dogrulabtn;
    private EditText telefonnumarasıgirdisi,dogrulamakodugirdisi;
  //telefon dogrulama
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mdogrulamaId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //yükleniyor penceresi
    private ProgressDialog yuklemebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefonoturum);
        //Tanımlar
        mAuth=FirebaseAuth.getInstance();

        dogrulamakodugonderbtn=findViewById(R.id.dogrulamagonderbtn);
        dogrulabtn=findViewById(R.id.dogrulabtn);

        telefonnumarasıgirdisi=findViewById(R.id.telefonnumarsıgirdisi);
        dogrulamakodugirdisi=findViewById(R.id.dogrulamakodugirdisi);

        //yukleniyor tanımlama

        yuklemebar=new ProgressDialog(this);

        dogrulamakodugonderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String telefonnumarası=telefonnumarasıgirdisi.getText().toString();
                if (TextUtils.isEmpty(telefonnumarası)){
                    Toast.makeText(TelefonoturumActivity.this,metin.an , Toast.LENGTH_LONG).show();
                }else{
                    //yukleniyor penceresi
                    yuklemebar.setTitle(metin.ao);
                    yuklemebar.setMessage(metin.ap);
                    yuklemebar.setCanceledOnTouchOutside(false);
                    yuklemebar.show();


                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(telefonnumarası)       
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(TelefonoturumActivity.this)                 
                                    .setCallbacks(mCallbacks)        
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        dogrulabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //görünürlük ayarlaması
                dogrulamakodugonderbtn.setVisibility(View.INVISIBLE);
                telefonnumarasıgirdisi.setVisibility(View.INVISIBLE);

                String dogrulamakodu=dogrulamakodugirdisi.getText().toString();
                if(TextUtils.isEmpty(dogrulamakodu)){
                    Toast.makeText(TelefonoturumActivity.this,metin.aq,Toast.LENGTH_LONG).show();
                }
                else{

                    //yukleniyor penceresi
                    yuklemebar.setTitle(metin.ar);
                    yuklemebar.setMessage(metin.at);
                    yuklemebar.setCanceledOnTouchOutside(false);
                    yuklemebar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mdogrulamaId, dogrulamakodu);
                    telefonlagirisyap(credential);

                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                   telefonlagirisyap(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                //yükleme penceresi
                yuklemebar.dismiss();

                Toast.makeText(TelefonoturumActivity.this,metin.au,Toast.LENGTH_LONG).show();

                //görünürlük ayarlaması
                dogrulamakodugonderbtn.setVisibility(View.VISIBLE);
                dogrulabtn.setVisibility(View.INVISIBLE);

                telefonnumarasıgirdisi.setVisibility(View.VISIBLE);
                dogrulamakodugirdisi.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mdogrulamaId = verificationId;
                mResendToken = token;

                //yükleme penceresi
                yuklemebar.dismiss();

                Toast.makeText(TelefonoturumActivity.this,metin.av,Toast.LENGTH_LONG).show();

                //görünürlük ayarlaması
                dogrulamakodugonderbtn.setVisibility(View.INVISIBLE);
                dogrulabtn.setVisibility(View.VISIBLE);

                telefonnumarasıgirdisi.setVisibility(View.INVISIBLE);
                dogrulamakodugirdisi.setVisibility(View.VISIBLE);
            }
        };

    }
    private void telefonlagirisyap(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                              yuklemebar.dismiss();
                              Toast.makeText(TelefonoturumActivity.this,metin.aw,Toast.LENGTH_LONG).show();
                              kullaniciyiAnasayfayagonder();
                        }
                        else {
                            String hatamesajı=task.getException().toString();
                            Toast.makeText(TelefonoturumActivity.this,metin.l+hatamesajı,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
 //anasayfa yerine ayarlar kısmında profil oluşturmaya yönlendiriliyor
    private void kullaniciyiAnasayfayagonder() {
        Intent anasayfa=new Intent(TelefonoturumActivity.this,ayarlar.class);
        anasayfa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(anasayfa);
        finish();
    }

}