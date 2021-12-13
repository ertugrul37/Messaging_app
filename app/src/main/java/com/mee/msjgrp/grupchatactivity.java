package com.mee.msjgrp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


public class grupchatactivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageButton mesajgondermebtn;
    private EditText kullanıcımesajgirdisi;
    private ScrollView mScrolview;
    private Toolbar supportActionBar;
    private TextView metinmesajlarinigöster;



   //firebase
    private DatabaseReference kullaniciyolu,grupAdiyolu,grupmesajadiyolu;
    private FirebaseAuth myetki;
    //intentyetki
    private String mevcutgrupadi,aktifkullanıcıId,aktifkullanıcıAdı,aktiftarih,aktifzaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupchatactivity);

        //intent al
        mevcutgrupadi = getIntent().getExtras().get("grupAdı").toString();
        Toast.makeText(this, mevcutgrupadi, Toast.LENGTH_LONG).show();

        //Firebase tanımlama
        myetki=FirebaseAuth.getInstance();
        aktifkullanıcıId=myetki.getCurrentUser().getUid();
        kullaniciyolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar");
        grupAdiyolu= FirebaseDatabase.getInstance().getReference().child("Grublar").child(mevcutgrupadi);



        //Tanımlar
        mtoolbar = findViewById(R.id.grupchatbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(mevcutgrupadi);

        mesajgondermebtn = findViewById(R.id.mesajgondermebtn);
        kullanıcımesajgirdisi = findViewById(R.id.grupmesajıgirdisi);
        metinmesajlarinigöster = findViewById(R.id.grup_chat_metni_gosterme);
        mScrolview = findViewById(R.id.scrolview);

       kullanicibilgisial();

    }
    @Override
    protected void onStart() {
        super.onStart();
        grupAdiyolu.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
              if (snapshot.exists()){
                  mesajlarıGöster(snapshot);
              }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    mesajlarıGöster(snapshot);
                }
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
    }

    private void mesajlarıGöster(DataSnapshot snapshot) {

        Iterator iterator=snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String sohbettarihi=(String) ((DataSnapshot)iterator.next()).getValue();
            String sohbetmesajı=(String) ((DataSnapshot)iterator.next()).getValue();
            String sohbetadi=(String) ((DataSnapshot)iterator.next()).getValue();
            String sohbetzamani=(String) ((DataSnapshot)iterator.next()).getValue();


            metinmesajlarinigöster.append(sohbettarihi+"( "+sohbetmesajı+" )\n\n");
            mScrolview.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }


    private void mesajıveritabinakaydet() {

        String mesaj=kullanıcımesajgirdisi.getText().toString();
        String mesajanahtari=grupAdiyolu.push().getKey();
        if (TextUtils.isEmpty(mesaj)){

            Toast.makeText(this, "Mesaj alanı boş olamaz", Toast.LENGTH_LONG).show();
        }else{
            Calendar tarihicintak=Calendar.getInstance();
            SimpleDateFormat aktiftarihformati=new SimpleDateFormat("MM dd,yyyy");
            aktiftarih=aktiftarihformati.format(tarihicintak.getTime());

            Calendar zamanicintakvim=Calendar.getInstance();
            SimpleDateFormat aktifzamanformat=new SimpleDateFormat("hh:mm:ss a");
            aktifzaman=aktifzamanformat.format(zamanicintakvim.getTime());

            HashMap<String,Object>grupmesajanahtari=new HashMap<>();
            grupAdiyolu.updateChildren(grupmesajanahtari);

            grupmesajadiyolu=grupAdiyolu.child(mesajanahtari);

            HashMap<String,Object>mesajbilgisiMap=new HashMap<>();
            mesajbilgisiMap.put("ad",aktifkullanıcıAdı);
            mesajbilgisiMap.put("mesaj",mesaj);
            mesajbilgisiMap.put("tarih",aktiftarih);
            mesajbilgisiMap.put("zaman",aktifzaman);

            grupmesajadiyolu.updateChildren(mesajbilgisiMap);
        }
    }
    private void kullanicibilgisial(){

        mesajgondermebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mesajıveritabinakaydet();
                kullanıcımesajgirdisi.setText("");
                mScrolview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        kullaniciyolu.child(aktifkullanıcıId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    aktifkullanıcıAdı=snapshot.child("ad").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}