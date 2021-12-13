package com.mee.msjgrp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ayarlar extends AppCompatActivity {

    private Button hesapayarlarınıgüncelle;
    private EditText kullanıcıadı,kullanıcıdurumu;
    private CircleImageView kullanıcıprofili;
    //yükleniyor
    private ProgressDialog yukleniyorBar;


    //resim seçme
    private static final int galerisecme=1;
    //firebase
    private StorageReference profilresmi;
    private StorageTask yuklemegörevi;
    private FirebaseAuth myetki;
    private DatabaseReference veriyoluuuu;
    private String mevcutkullanıcıId;


    //toolbar tanım
    private Toolbar ayarlartoolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);

        profilresmi = FirebaseStorage.getInstance().getReference().child("Profil Resimleri");
         //kontrol tanımlamaları
        hesapayarlarınıgüncelle=findViewById(R.id.ayarlarıguncelleme);
        kullanıcıadı=findViewById(R.id.kullanıcıadıayarla);
        kullanıcıdurumu=findViewById(R.id.profildurumuayarla);
        kullanıcıprofili=findViewById(R.id.profilresmiayarla);

        //toolbar
        ayarlartoolbar=findViewById(R.id.ayarlar_toolbar);
        setSupportActionBar(ayarlartoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profil ayarları");

        //yükleniyor
        yukleniyorBar=new ProgressDialog(this);

          myetki=FirebaseAuth.getInstance();
          veriyoluuuu= FirebaseDatabase.getInstance().getReference();
          mevcutkullanıcıId=myetki.getCurrentUser().getUid();
          hesapayarlarınıgüncelle.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  hesapayarlarınıgüncelle();
              }
          });
          kullanıcıadı.setVisibility(View.INVISIBLE);



          Kullanıcıbilgisial();

          kullanıcıprofili.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  //crop kırpma aktivity açma
                  CropImage.activity()
                          .setGuidelines(CropImageView.Guidelines.ON)
                          .setAspectRatio(1,1)
                          .start(ayarlar.this);


              }
          });

    }
    private String dosyauzantısıal(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //resim seçme kodu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK)
        {
            //Resim seçiliyorsa
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            resimUri=result.getUri();
            kullanıcıprofili.setImageURI(resimUri);
        }else
        {
            Toast.makeText(this,"Resim seçilemedi",Toast.LENGTH_LONG).show();
        }

    }
    //Uri
    Uri resimUri;
    String myUri="";



    private void Kullanıcıbilgisial() {

        veriyoluuuu.child("kullanicilar").child(mevcutkullanıcıId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())&&(snapshot.hasChild("ad"))&&(snapshot.hasChild("resim")))
                {
                    String kullaniciAdial=snapshot.child("ad").getValue().toString();
                    String kullaniciDurumual=snapshot.child("durum").getValue().toString();
                    String kullaniciResmial=snapshot.child("resim").getValue().toString();
                    Picasso.get().load(kullaniciResmial).into(kullanıcıprofili);
                    kullanıcıadı.setText(kullaniciAdial);
                    kullanıcıdurumu.setText(kullaniciAdial);


                }
               else if ((snapshot.exists())&&(snapshot.hasChild("ad")))
                {
                    String kullaniciAdial=snapshot.child("ad").getValue().toString();
                    String kullaniciDurumual=snapshot.child("durum").getValue().toString();
                    String kullaniciResmial=snapshot.child("resim").getValue().toString();

                    kullanıcıadı.setText(kullaniciAdial);
                    kullanıcıdurumu.setText(kullaniciAdial);



                }else
                {
                    kullanıcıadı.setVisibility(View.VISIBLE);
                    Toast.makeText(ayarlar.this,"Lütfen Profilinizi güncelleyiniz",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void hesapayarlarınıgüncelle(){
       String kullaniciadinial=kullanıcıadı.getText().toString();
        String kullanicidurumunual=kullanıcıdurumu.getText().toString();

        if (TextUtils.isEmpty(kullaniciadinial))
        {
            Toast.makeText(this,"Ad boş olamaz",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(kullanicidurumunual))
        {
            Toast.makeText(this,"Durum boş olamaz",Toast.LENGTH_LONG).show();
        }
        else
        {
            resimYukle();
        }
    }
    private void resimYukle() {


        yukleniyorBar.setTitle("Bilgi aktarma");
        yukleniyorBar.setMessage("Lütfen bekleyin");
        yukleniyorBar.setCanceledOnTouchOutside(false);
        yukleniyorBar.show();

        if (resimUri==null)
        {

            DatabaseReference veriyolu=FirebaseDatabase.getInstance().getReference().child("kullanicilar");
            String gonderId=veriyolu.push().getKey();
            String kullaniciAdial=kullanıcıadı.getText().toString();
            String kullaniciDurumual=kullanıcıdurumu.getText().toString();

            HashMap<String,Object>profilharitası=new HashMap<>();
            profilharitası.put("uid",gonderId);
            profilharitası.put("ad",kullaniciAdial);
            profilharitası.put("durum",kullaniciDurumual);


            veriyolu.child(mevcutkullanıcıId).updateChildren(profilharitası);

            yukleniyorBar.dismiss();

        }
        else
        {
            final StorageReference resimyolu=profilresmi.child(mevcutkullanıcıId+"."+dosyauzantısıal(resimUri));
            yuklemegörevi=resimyolu.putFile(resimUri);
            yuklemegörevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return resimyolu.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    //GÖREVTAMAMLANDIĞINDA

                    if (task.isSuccessful())
                    {
                        //başaralı ise
                        Uri indirmeUrisi=task.getResult();
                        myUri=indirmeUrisi.toString();
                        DatabaseReference veriyolu=FirebaseDatabase.getInstance().getReference().child("kullanicilar");
                        String gonderId=veriyolu.push().getKey();
                        String kullaniciAdial=kullanıcıadı.getText().toString();
                        String kullaniciDurumual=kullanıcıdurumu.getText().toString();

                        HashMap<String,Object>profilharitası=new HashMap<>();
                        profilharitası.put("uid",gonderId);
                        profilharitası.put("ad",kullaniciAdial);
                        profilharitası.put("durum",kullaniciDurumual);
                        profilharitası.put("resim",myUri);

                        veriyolu.child(mevcutkullanıcıId).updateChildren(profilharitası);

                        yukleniyorBar.dismiss();
                    }
                    else
                    {
                        String hata = task.getException().toString();
                        Toast.makeText(ayarlar.this,"HATA "+hata,Toast.LENGTH_SHORT).show();
                        yukleniyorBar.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ayarlar.this,"HATA"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    yukleniyorBar.dismiss();
                }
            });

        }


    }
}