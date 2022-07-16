package com.mee.msjgrp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {
    Strings metin = new Strings();
    private Toolbar actionbar;
    private ViewPager vpMain;
    private TabLayout tabsMain;
    private TabsAdapter tabsAdapter;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference kullanıcılarreference;


    public void init() {
        actionbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionbar);
        getSupportActionBar().setTitle(R.string.app_name);

        //firebase
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        kullanıcılarreference = FirebaseDatabase.getInstance().getReference("");

        vpMain = (ViewPager) findViewById(R.id.vpMain);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        vpMain.setAdapter(tabsAdapter);

        tabsMain = (TabLayout) findViewById(R.id.tabsMain);
        tabsMain.setupWithViewPager(vpMain);
    }

    private void kullanıcıvarlıgınıdogrula() {
        String mevcutkullanıcıID = auth.getCurrentUser().getUid();
        kullanıcılarreference.child("kullanicilar").child(mevcutkullanıcıID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("ad").exists())) {
                    Toast.makeText(MainActivity2.this, metin.v, Toast.LENGTH_SHORT).show();
                } else {
                    Intent ayarlar = new Intent(MainActivity2.this, com.mee.msjgrp.ayarlar.class);
                    ayarlar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(ayarlar);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            Intent welcomeIntent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(welcomeIntent);
            finish();
        } else {
            kullanıcıvarlıgınıdogrula();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menumain, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.Arkadasbul) {
            Intent arkadasBul = new Intent(MainActivity2.this, Arkadasbul.class);
            startActivity(arkadasBul);
        }
        if (item.getItemId() == R.id.anagrubolustur) {
            yenigurubtalebi();
        }
        if (item.getItemId() == R.id.Ayarlar) {
            Intent ayarlartxt = new Intent(MainActivity2.this, ayarlar.class);
            startActivity(ayarlartxt);
        }
        if (item.getItemId() == R.id.mainLogout) {
            auth.signOut();
            Intent loginIntent = new Intent(MainActivity2.this, hesapvar.class);
            startActivity(loginIntent);
            finish();
        }
        return true;
    }
    private void yenigurubtalebi() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this, R.style.Alertdialog);
        builder.setTitle(metin.w);

        final EditText grupAdialani = new EditText(MainActivity2.this);
        grupAdialani.setHint(metin.x);
        builder.setView(grupAdialani);
        builder.setPositiveButton(metin.y, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String grupadi = grupAdialani.getText().toString();
                if (TextUtils.isEmpty(grupadi)) {
                    Toast.makeText(MainActivity2.this, metin.z, Toast.LENGTH_LONG).show();
                } else {
                    YeniGrubOlustur(grupadi);
                }
            }
        });
        builder.setNegativeButton(metin.aa, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
    private void YeniGrubOlustur(String grupadi) {
        kullanıcılarreference.child("Grublar").child(grupadi).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity2.this, grupadi + metin.ab, Toast.LENGTH_SHORT).show();
                        }
                 }
          });
    }
}