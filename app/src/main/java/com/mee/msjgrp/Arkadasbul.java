package com.mee.msjgrp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import model.kisiler;

public class Arkadasbul extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView Arkadasbullisetesi;

    //firebase
    private DatabaseReference kullainiciyolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arkadasbul);

        Arkadasbullisetesi=findViewById(R.id.arkadasbullistesi);
        Arkadasbullisetesi.setLayoutManager(new LinearLayoutManager(this));

        mToolbar=findViewById(R.id.arkdasbul);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Arkadaş Bul");


        //firebasetanılaması
        kullainiciyolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar");

    }

    @Override
    protected void onStart() {
        super.onStart();

        //başladığında ne yapsın

        //sorgu-seçenekler
        FirebaseRecyclerOptions<kisiler>secenekler=
                new FirebaseRecyclerOptions.Builder<kisiler>()
                .setQuery(kullainiciyolu,kisiler.class)
                .build();

        FirebaseRecyclerAdapter<kisiler,ArkadasBulViewHolder> adapter = new
                FirebaseRecyclerAdapter<kisiler, ArkadasBulViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder
                    (@NonNull ArkadasBulViewHolder arkdasBulViewHolder,

                     @SuppressLint("RecyclerView") final int position, @NonNull kisiler kisilerm) {

                arkdasBulViewHolder.kullaniciAdi.setText(kisilerm.getAd());
                arkdasBulViewHolder.kullanicidurumu.setText(kisilerm.getDurum());
                Picasso.get().load(kisilerm.getResim()).into(arkdasBulViewHolder.profilResmi);

                //Tıklandığında
                arkdasBulViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Tıklanankullanicliarıdgoster=getRef(position).getKey();

                        Intent profilAktivite=new Intent(Arkadasbul.this,profilActivity.class);

                        profilAktivite.putExtra("Tıklanankullanicliarıdgoster",
                                Tıklanankullanicliarıdgoster);

                        startActivity(profilAktivite);

                    }
                });

            }

            @NonNull
            @Override
            public ArkadasBulViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanicigosterme,parent,false);
                ArkadasBulViewHolder bulviewHolder=new ArkadasBulViewHolder(view);

                return bulviewHolder;
            }
        };
        Arkadasbullisetesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }
    public static class ArkadasBulViewHolder extends RecyclerView.ViewHolder
    {
        TextView kullaniciAdi,kullanicidurumu;
        CircleImageView profilResmi;

        public ArkadasBulViewHolder(@NonNull View itemView) {
            super(itemView);


            //Tanımlamalar
            kullaniciAdi=itemView.findViewById(R.id.kullaniciAdi);
            kullanicidurumu=itemView.findViewById(R.id.kullanicidurumu);
            profilResmi=itemView.findViewById(R.id.kullaniciresmi);
        }
    }
}