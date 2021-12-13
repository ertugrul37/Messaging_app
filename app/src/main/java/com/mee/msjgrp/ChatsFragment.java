package com.mee.msjgrp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private View Ozelsohbetler;
    private RecyclerView sohbetlerlistesi;


     //FİREBASE
    private DatabaseReference sohbetyolu,kullanici_yolu;
    private FirebaseAuth mYetki;
    private String aktifkullaniciID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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
        Ozelsohbetler= inflater.inflate(R.layout.fragment_chats, container, false);

        //FİREBASE
        mYetki=FirebaseAuth.getInstance();
        aktifkullaniciID=mYetki.getCurrentUser().getUid();
        sohbetyolu= FirebaseDatabase.getInstance().getReference().child("sohbetler").child(aktifkullaniciID);
        kullanici_yolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar");

        //Rceycler
        sohbetlerlistesi=Ozelsohbetler.findViewById(R.id.sohbetlerlistesi);
        sohbetlerlistesi.setLayoutManager(new LinearLayoutManager(getContext()));


        return Ozelsohbetler;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions <kisiler>secenekler=new FirebaseRecyclerOptions.Builder<kisiler>()
        .setQuery(sohbetyolu,kisiler.class)
        .build();

        FirebaseRecyclerAdapter<kisiler,SohbetlerViewholder>adapter=new FirebaseRecyclerAdapter<kisiler, SohbetlerViewholder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull SohbetlerViewholder sohbetlerViewholder, int i, @NonNull kisiler kisiler)

            {
                final String kullaniciIDleri=getRef(i).getKey();
                final String[] Resimal = {"varsayılanResim"};

                //veri tabanından veri çağırma

                kullanici_yolu.child(kullaniciIDleri).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            if (snapshot.hasChild("resim"))
                            {
                                 Resimal[0] =snapshot.child("resim").getValue().toString();

                                //veri tabanından gelen resmi kontrole aktarma
                                Picasso.get().load(Resimal[0]).into(sohbetlerViewholder.profilresmi);
                            }
                            final String Adal=snapshot.child("ad").getValue().toString();
                            final String Durumal=snapshot.child("durum").getValue().toString();

                            //veri tabanından gelen adı ve durumu kontrole aktarma

                            sohbetlerViewholder.kullaniciAdi.setText(Adal);
                            sohbetlerViewholder.kullanicidurumu.setText("Son görülme: "+"\n"+"Tarih"+ "Zaman");

                            //HER SATIRA TIKLANDIĞINDA NE YAPSIN
                            sohbetlerViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    //CHAT AKTİVETİSİNE GİDECEK INTENTLE VERİ GÖNDER
                                    Intent chataktivite=new Intent(getContext(),chatActivity.class);
                                    chataktivite.putExtra("kullanici_ID_ziyaret",kullaniciIDleri);
                                    chataktivite.putExtra("kullanici_adı_ziyaret",Adal);
                                    chataktivite.putExtra("resim_ziyaret", Resimal[0]);
                                    startActivity(chataktivite);
                                }
                            });
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
            public SohbetlerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanicigosterme,parent,false);
                return new SohbetlerViewholder(view);


            }
        };
        sohbetlerlistesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class SohbetlerViewholder extends RecyclerView.ViewHolder
    {
        //kontroller
        CircleImageView profilresmi;
        TextView kullaniciAdi,kullanicidurumu;

        public SohbetlerViewholder(@NonNull View itemView) {
            super(itemView);
            //KONTROL TANIMLAMALARI
            profilresmi=itemView.findViewById(R.id.kullaniciresmi);
            kullaniciAdi=itemView.findViewById(R.id.kullaniciAdi);
            kullanicidurumu=itemView.findViewById(R.id.kullanicidurumu);
        }
    }
}