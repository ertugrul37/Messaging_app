package com.mee.msjgrp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button hesapvarbtn,yenihesapbtn;
    public void init()
    {
        hesapvarbtn=(Button) findViewById(R.id.hesapvarbtn);
        yenihesapbtn=(Button) findViewById(R.id.yenihesapbtn);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

      hesapvarbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              Intent intenthesapvar=new Intent(MainActivity.this,hesapvar.class);
              startActivity(intenthesapvar);
              finish();

          }
      });
      yenihesapbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              Intent intentyenihesap=new Intent(MainActivity.this,yenihesapvar.class);
              startActivity(intentyenihesap);
             finish();
          }
      });
    }
}