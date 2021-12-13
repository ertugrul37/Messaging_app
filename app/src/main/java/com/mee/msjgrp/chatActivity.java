package com.mee.msjgrp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MesajAdaptor;
import de.hdodenhof.circleimageview.CircleImageView;
import model.mesajlarmodeli;

public class chatActivity extends AppCompatActivity {

    private String IDmesajalici,admesajialici,resimmesajıalici,Idmesajgonderen;
    private TextView kullaniciadi,kullancisongorolmesi;
    private CircleImageView kullaniciresmi;
    private ImageView sohbetlergonderme;
    private Toolbar sohbettoolbar;
    private ImageButton mesajgondermebutonu;
    private EditText mesajgirilenmetni;
    private FirebaseAuth mYetki;
    private DatabaseReference mesajyolu;
    private final List<mesajlarmodeli> MesajlarList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MesajAdaptor mesajAdaptor;
    private RecyclerView kullanicimesajlarilisetesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //chat fragmentden gelen Intentleri alma
        IDmesajalici=getIntent().getExtras().get("kullanici_ID_ziyaret").toString();
        admesajialici=getIntent().getExtras().get("kullanici_adı_ziyaret").toString();
        resimmesajıalici=getIntent().getExtras().get("resim_ziyaret").toString();

        //Tanımlamalar



        kullaniciadi=findViewById(R.id.mesajekranıkullanıcıadı);
        kullancisongorolmesi=findViewById(R.id.mesajekranısongorülme);
        kullaniciresmi=findViewById(R.id.mesajekranıprofilresmi);
        sohbetlergonderme=findViewById(R.id.sohbetegondermeresmi);
        mesajgondermebutonu=findViewById(R.id.chatmesajgöderbutonu);
        mesajgirilenmetni=findViewById(R.id.girilen_mesaj);
        mesajAdaptor= new MesajAdaptor(MesajlarList);
        kullanicimesajlarilisetesi=findViewById(R.id.kullanicilarin_özel_mesajların_listesi);
        linearLayoutManager=new LinearLayoutManager(this);
        kullanicimesajlarilisetesi.setLayoutManager(linearLayoutManager);
        kullanicimesajlarilisetesi.setAdapter(mesajAdaptor);

        mYetki=FirebaseAuth.getInstance();
        mesajyolu= FirebaseDatabase.getInstance().getReference();
        Idmesajgonderen=mYetki.getCurrentUser().getUid();


         sohbetlergonderme.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent sohbetler= new Intent(chatActivity.this,MainActivity2.class);
                 startActivity(sohbetler);
             }
         });

        //kontrolleri Intenle gelenleri aktarma
        kullaniciadi.setText(admesajialici);
        Picasso.get().load(resimmesajıalici).placeholder(R.drawable.icons8).into(kullaniciresmi);

        mesajgondermebutonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               mesajgonder();
            }
        });
    }

    @Override
    protected void onStart() {
        mesajyolu.child("Mesajlar").child(Idmesajgonderen).child(IDmesajalici)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        mesajlarmodeli mesajlar=snapshot.getValue(mesajlarmodeli.class);
                        MesajlarList.add(mesajlar);
                        mesajAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        super.onStart();
    }

    private void mesajgonder()

    {
       String mesajMetni = mesajgirilenmetni.getText().toString();
       if (TextUtils.isEmpty(mesajMetni))
       {
           Toast.makeText(this,"Mesaj yazmanız gerekiyor",Toast.LENGTH_LONG).show();
       }
       else
       {
           String mesajgondereneyolu="Mesajlar/"+Idmesajgonderen+"/"+IDmesajalici;
           String mesajalanyolu="Mesajlar/"+IDmesajalici+"/"+Idmesajgonderen;

           DatabaseReference kullanicimesajAnahtaerolu=mesajyolu.child("Mesajlar").child(Idmesajgonderen).child(IDmesajalici).push();

           String mesajeklemeId=kullanicimesajAnahtaerolu.getKey();

           Map mesajmetniGovdesi = new HashMap();
           mesajmetniGovdesi.put("mesaj",mesajMetni);
           mesajmetniGovdesi.put("tur","metin");
           mesajmetniGovdesi.put("kimden",Idmesajgonderen);

           Map mesajGovedesidetaylari=new HashMap();
           mesajGovedesidetaylari.put(mesajgondereneyolu+"/"+mesajeklemeId,mesajmetniGovdesi);
           mesajGovedesidetaylari.put(mesajalanyolu+"/"+mesajeklemeId,mesajmetniGovdesi);

           mesajyolu.updateChildren(mesajGovedesidetaylari).addOnCompleteListener(new OnCompleteListener() {
               @Override
               public void onComplete(@NonNull Task task)
               {
                   if (task.isSuccessful())
                   {
                       Toast.makeText(chatActivity.this, "Mesaj gönderildi", Toast.LENGTH_LONG).show();
                   }
                   else
                   {
                       Toast.makeText(chatActivity.this, "Mesaj gönderme hatalı", Toast.LENGTH_SHORT).show();
                   }
                   mesajgirilenmetni.setText("");
               }
           });


       }
    }
}