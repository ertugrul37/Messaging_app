package com.mee.msjgrp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import model.kisiler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaleplerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaleplerFragment extends Fragment {

    private View Taleplerfragmanetkontrol;
    private RecyclerView taleplerlistem;
    private DatabaseReference sohbeettalebleriyolu,kullanicilaryolu,sohbetleryolu;
    private FirebaseAuth mYetki;
    private String aktifkullaniciId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaleplerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaleplerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaleplerFragment newInstance(String param1, String param2) {
        TaleplerFragment fragment = new TaleplerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Taleplerfragmanetkontrol= inflater.inflate(R.layout.fragment_talepler, container, false);
        mYetki=FirebaseAuth.getInstance();
        aktifkullaniciId=mYetki.getCurrentUser().getUid();
        //FİREBASE
        sohbeettalebleriyolu= FirebaseDatabase.getInstance().getReference().child("sohbet talebi");
        kullanicilaryolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar");
        sohbetleryolu= FirebaseDatabase.getInstance().getReference().child("sohbetler");
        //recycler
        taleplerlistem=Taleplerfragmanetkontrol.findViewById(R.id.chattaleplerilistesi);
        taleplerlistem.setLayoutManager(new LinearLayoutManager(getContext()));
        return Taleplerfragmanetkontrol;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<kisiler>secenekler = new FirebaseRecyclerOptions.Builder<kisiler>()
                .setQuery(sohbeettalebleriyolu.child(aktifkullaniciId),kisiler.class)
                .build();

        FirebaseRecyclerAdapter<kisiler,TaleplerViewholder>adapter=new FirebaseRecyclerAdapter<kisiler, TaleplerViewholder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull TaleplerViewholder taleplerViewholder, int i, @NonNull kisiler kisiler) {

                //butonları gösterme
                taleplerViewholder.itemView.findViewById(R.id.talepkabulbutonu).setVisibility(View.VISIBLE);
                taleplerViewholder.itemView.findViewById(R.id.talepiptalbutonu).setVisibility(View.VISIBLE);

                //Taleplerin hepsini alma
                final String kullanici_id_listesi=getRef(i).getKey();
                DatabaseReference talepturual=getRef(i).child("talep_turu").getRef();
                talepturual.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            String tur=snapshot.getValue().toString();
                            if (tur.equals("alındı"))
                            {
                                kullanicilaryolu.child(kullanici_id_listesi).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if (snapshot.hasChild("resim"))
                                        {

                                            //veri tabanından resmleri çekip değişkenlere aktarma

                                            final String talepkullaniciresmi=snapshot.child("resim").getValue().toString();

                                            //çekilen resimleri ilgili kontrollere aktarma

                                            Picasso.get().load(talepkullaniciresmi).into(taleplerViewholder.profilresmi);

                                        }


                                        //veri tabanından verileri çekip değişkenlere aktarma
                                        final String talepkullaniciadi=snapshot.child("ad").getValue().toString();
                                        final String talepkullanicidurumu=snapshot.child("durum").getValue().toString();


                                        //çekilen verileri ilgili kontrollere aktarma
                                        taleplerViewholder.kullaniciadi.setText(talepkullaniciadi);
                                        taleplerViewholder.kullanicidurumu.setText("kullanıcı seninle iletişim kurmak istiyor");


                                        //her satıra tıklandığında ne yapsın
                                        taleplerViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                CharSequence secenekler[]=new CharSequence[]
                                                        {
                                                                "Kabul",
                                                                "İptal"
                                                        };
                                                //ALERTDİALOG
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(talepkullaniciadi+" Mesaj talebi");

                                                builder.setItems(secenekler, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i)
                                                    {
                                                        if (i==0)
                                                        {
                                                            sohbetleryolu.child(aktifkullaniciId).child(kullanici_id_listesi).child("sohbetler")
                                                                    .setValue("Kaydedildi").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                sohbetleryolu.child(kullanici_id_listesi).child(aktifkullaniciId)
                                                                                        .child("sohbetler").setValue("Kaydedildi")
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    sohbeettalebleriyolu.child(aktifkullaniciId).child(kullanici_id_listesi)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                                {
                                                                                                                    if (task.isSuccessful())
                                                                                                                    {
                                                                                                                        sohbetleryolu.child(kullanici_id_listesi).child(aktifkullaniciId)
                                                                                                                                .removeValue()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                                                    {
                                                                                                                                        Toast.makeText(getContext(), "Sohbet Kaydedildi", Toast.LENGTH_LONG).show();

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
                                                        if (i==1)
                                                        {
                                                            sohbeettalebleriyolu.child(aktifkullaniciId).child(kullanici_id_listesi)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                sohbetleryolu.child(kullanici_id_listesi).child(aktifkullaniciId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                Toast.makeText(getContext(), "Sohbet Silindi", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });


                                                        }
                                                    }
                                                });
                                                builder.show();

                                            }

                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {

                                    }
                                });
                            }
                            else if(tur.equals("gonderildi")){

                                Button talep_gonderme_btn = taleplerViewholder.item.findViewById(R.id.talepkabulbutonu);
                                talep_gonderme_btn.setText("Talep Gonderildi");
                                taleplerViewholder.itemView.findViewById(R.id.talepiptalbutonu).setVisibility(View.INVISIBLE);

                                //YENİ KISIM
                                kullanicilaryolu.child(kullanici_id_listesi).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if (snapshot.hasChild("resim"))
                                        {

                                            //veri tabanından resmleri çekip değişkenlere aktarma

                                            final String talepkullaniciresmi=snapshot.child("resim").getValue().toString();

                                            //çekilen resimleri ilgili kontrollere aktarma

                                            Picasso.get().load(talepkullaniciresmi).into(taleplerViewholder.profilresmi);

                                        }


                                        //veri tabanından verileri çekip değişkenlere aktarma
                                        final String talepkullaniciadi=snapshot.child("ad").getValue().toString();
                                        final String talepkullanicidurumu=snapshot.child("durum").getValue().toString();


                                        //çekilen verileri ilgili kontrollere aktarma
                                        taleplerViewholder.kullaniciadi.setText(talepkullaniciadi);
                                        taleplerViewholder.kullanicidurumu.setText("sen "+talepkullaniciadi +" adlı kullanıcıya talep gönderdin");


                                        //her satıra tıklandığında ne yapsın
                                        taleplerViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                CharSequence secenekler[]=new CharSequence[]
                                                        {
                                                                "Chat talebini iptal et"
                                                        };
                                                //ALERTDİALOG
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Mevcut chat talebi");

                                                builder.setItems(secenekler, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i)
                                                    {

                                                        if (i==0)
                                                        {
                                                            sohbeettalebleriyolu.child(aktifkullaniciId).child(kullanici_id_listesi)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                sohbetleryolu.child(kullanici_id_listesi).child(aktifkullaniciId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                Toast.makeText(getContext(), "Chat talebeniz Silindi", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });


                                                        }
                                                    }
                                                });
                                                builder.show();

                                            }

                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
            }

            @NonNull
            @Override
            public TaleplerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanicigosterme,parent,false);
                TaleplerViewholder holder=new TaleplerViewholder(view);
                return holder;

            }
        };
        taleplerlistem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class TaleplerViewholder extends RecyclerView.ViewHolder
    {
        public View item;
        //kontroller
        TextView kullaniciadi,kullanicidurumu;
        CircleImageView profilresmi;
        Button kabulbutonu,iptalbutonu;

        public TaleplerViewholder(@NonNull View itemView) {
            super(itemView);

            //kontrol tanımlamaları
            kullaniciadi=itemView.findViewById(R.id.kullaniciAdi);
            kullanicidurumu=itemView.findViewById(R.id.kullanicidurumu);
            profilresmi=itemView.findViewById(R.id.kullaniciresmi);

            kabulbutonu=itemView.findViewById(R.id.talepkabulbutonu);
            iptalbutonu=itemView.findViewById(R.id.talepiptalbutonu);
        }
    }
}