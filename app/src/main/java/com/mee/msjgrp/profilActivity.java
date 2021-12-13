package com.mee.msjgrp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profilActivity extends AppCompatActivity {

    private String alinankullaniciId,aktifdurum,aktifkullaniciID;

    private CircleImageView kullaniciprofilresmi;
    private TextView kullaniciprofiladi,kullaniciprofildurumu;
    private Button mesajgöndermetalebibutonu,mesajtalebiiptalbtn;

    //FİREBASEE
    private DatabaseReference kullaniciyolu,sohbettalebiyolu,sohbetleryolu;
    FirebaseAuth mYetki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        alinankullaniciId=getIntent().getExtras().get("Tıklanankullanicliarıdgoster").toString();

        //Tanımlamalar
        kullaniciprofilresmi=findViewById(R.id.profilresmiziyaret);
        kullaniciprofiladi=findViewById(R.id.profilziyaretkullaniciadi);
        kullaniciprofildurumu=findViewById(R.id.profilziyaretdurumu);
        mesajgöndermetalebibutonu=findViewById(R.id.mesajgondermetalebibtn);
        mesajtalebiiptalbtn=findViewById(R.id.mesajtalebiiptal);

        aktifdurum="yeni";

        //firebase tanımlama
        kullaniciyolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar");
        sohbettalebiyolu= FirebaseDatabase.getInstance().getReference().child("sohbet talebi");
        sohbetleryolu= FirebaseDatabase.getInstance().getReference().child("sohbetler");
        mYetki=FirebaseAuth.getInstance();


        aktifkullaniciID=mYetki.getCurrentUser().getUid();

        kullanicibilgisial();
    }

    private void kullanicibilgisial() {

        kullaniciyolu.child(alinankullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.exists())&&(snapshot.hasChild("resim")))
                {
                    //veritabanından verileri çekip değişkenlere aktarma
                    String kullaniciresmi=snapshot.child("resim").getValue().toString();
                    String kullaniciADİ=snapshot.child("ad").getValue().toString();
                    String kullanicidurumu=snapshot.child("durum").getValue().toString();


                    //verileri kontrollere aktarma
                    Picasso.get().load(kullaniciresmi).placeholder(R.drawable.icons8).into(kullaniciprofilresmi);
                    kullaniciprofiladi.setText(kullaniciADİ);
                    kullaniciprofildurumu.setText(kullanicidurumu);
                    //chat Talebi göderme metodu
                    chattalepleriniyönet();
                    
                }
                else
                {
                    //veritabanından verileri çekip değişkenlere aktarma
                    String kullaniciADİ=snapshot.child("ad").getValue().toString();
                    String kullanicidurumu=snapshot.child("durum").getValue().toString();


                    //verileri kontrollere aktarma
                    kullaniciprofiladi.setText(kullaniciADİ);
                    kullaniciprofildurumu.setText(kullanicidurumu);
                    chattalepleriniyönet();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void chattalepleriniyönet() {
    //talep varsa buton iptali göstersin
        sohbettalebiyolu.child(aktifkullaniciID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(alinankullaniciId))
                {
                    String talep_turu=snapshot.child(alinankullaniciId).child("talep_turu").getValue().toString();
                    if (talep_turu.equals("gonderildi"))
                    {

                        aktifdurum="talep_gonderildi";
                        mesajgöndermetalebibutonu.setText("MESAJ TALEBİ İPTAL");
                    }else
                    {
                        aktifdurum="talep_alindi";
                        mesajgöndermetalebibutonu.setText("MESAJ TALEBİ KABUL");
                        mesajtalebiiptalbtn.setVisibility(View.VISIBLE);
                        mesajtalebiiptalbtn.setEnabled(true);

                        mesajtalebiiptalbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Mesajtalebiİptal();
                            }
                        });
                    }
                }
                else
                {
                    sohbetleryolu.child(aktifkullaniciID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(alinankullaniciId))
                            {
                                 aktifdurum="arkadaşlar";
                                 mesajgöndermetalebibutonu.setText("BU SOHBETİ SİL");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (aktifkullaniciID.equals(alinankullaniciId))
        {
            //Butonu sakla
            mesajgöndermetalebibutonu.setVisibility(View.INVISIBLE);

        }
        else
        {
            //Mesajtalebi gitsin
            mesajgöndermetalebibutonu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mesajgöndermetalebibutonu.setEnabled(false);
                    if (aktifdurum.equals("yeni"))
                    {
                        sohbettalebigonder();
                    }
                    if (aktifdurum.equals("talep_gonderildi"))
                    {
                        Mesajtalebiİptal();
                    }
                    if (aktifdurum.equals("talep_alindi"))
                    {
                        MesajtalebiKabul();
                    }
                    if (aktifdurum.equals("arkadaşlar"))
                    {
                        ozelSohbetisil();
                    }
                }
            });

        }

    }

    private void ozelSohbetisil()
    {
        //sohbetisill
        sohbetleryolu.child(aktifkullaniciID).child(alinankullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    sohbetleryolu.child(alinankullaniciId).child(aktifkullaniciID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                mesajgöndermetalebibutonu.setEnabled(true);
                                aktifdurum="yeni";
                                mesajgöndermetalebibutonu.setText("MESAJ TALEBİ GÖNDER");

                                mesajtalebiiptalbtn.setVisibility(View.INVISIBLE);
                                mesajtalebiiptalbtn.setEnabled(false);

                            }
                        }
                    });
                }

            }
        });
    }

    private void MesajtalebiKabul() {
        sohbetleryolu.child(aktifkullaniciID).child(alinankullaniciId).child("sohbetler").setValue("Kaydedildi")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                       if (task.isSuccessful())
                       {
                           sohbetleryolu.child(alinankullaniciId).child(aktifkullaniciID).child("sohbetler").setValue("Kaydedildi")
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful())
                                           {
                                               sohbettalebiyolu.child(aktifkullaniciID).child(alinankullaniciId)
                                                       .removeValue()
                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task)
                                                           {
                                                               if (task.isSuccessful())
                                                               {
                                                                   sohbettalebiyolu.child(alinankullaniciId).child(aktifkullaniciID)
                                                                           .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task)
                                                                       {
                                                                           mesajgöndermetalebibutonu.setEnabled(true);
                                                                           aktifdurum = "arkadaşlar";
                                                                           mesajgöndermetalebibutonu.setText("BU SOHBETİ SİL");

                                                                           mesajtalebiiptalbtn.setVisibility(View.INVISIBLE);
                                                                           mesajtalebiiptalbtn.setEnabled(false);
                                                                       }
                                                                   });
                                                               }
                                                           }
                                                       });
                                           }
                                       }
                                   });
                       }
                    }
                });
    }

    private void Mesajtalebiİptal() {
        //Talebi gönderenden sil
        sohbettalebiyolu.child(aktifkullaniciID).child(alinankullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    sohbettalebiyolu.child(alinankullaniciId).child(aktifkullaniciID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                         if (task.isSuccessful())
                         {
                             mesajgöndermetalebibutonu.setEnabled(true);
                             aktifdurum="yeni";
                             mesajgöndermetalebibutonu.setText("MESAJ TALEBİ GÖNDER");

                             mesajtalebiiptalbtn.setVisibility(View.INVISIBLE);
                             mesajtalebiiptalbtn.setEnabled(false);

                         }
                        }
                    });
                }

            }
        });


    }

    private void sohbettalebigonder() {

        sohbettalebiyolu.child(aktifkullaniciID).child(alinankullaniciId).child("talep_turu").setValue("gonderildi")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            sohbettalebiyolu.child(alinankullaniciId).child(aktifkullaniciID).child("talep_turu")
                                    .setValue("alındı").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        mesajgöndermetalebibutonu.setEnabled(true);
                                        aktifdurum="talep_gonderildi";
                                        mesajgöndermetalebibutonu.setText("Sohbet Talebi İptal");
                                    }
                                }
                            });
                        }
                    }
                });
    }
}