package com.mee.msjgrp;

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
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {


     private View kisilerView;
     private RecyclerView kisilerlistem;
     private String aktifkullaniciId;

     //---firebase
    private DatabaseReference Sohbetleryolu,kullanıclaryolu;
    private FirebaseAuth mYetki;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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


        //RECYCLER
        kisilerView= inflater.inflate(R.layout.fragment_friends, container, false);

        kisilerlistem=kisilerView.findViewById(R.id.kisilerlistem);
        kisilerlistem.setLayoutManager(new LinearLayoutManager(getContext()));

        //firebase
        mYetki=FirebaseAuth.getInstance();
        aktifkullaniciId=mYetki.getCurrentUser().getUid();

        Sohbetleryolu= FirebaseDatabase.getInstance().getReference().child("sohbetler").child(aktifkullaniciId);
        kullanıclaryolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar");
        return kisilerView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions secenekler=new FirebaseRecyclerOptions.Builder<kisiler>()
                .setQuery(Sohbetleryolu,kisiler.class)
                .build();

        //adapter
        FirebaseRecyclerAdapter<kisiler,KisilerViewholder>adapter=new FirebaseRecyclerAdapter<kisiler, KisilerViewholder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull KisilerViewholder holder, int positon, @NonNull kisiler kisilerin) {
               String tıklanansatırkullaniciID=getRef(positon).getKey();
               kullanıclaryolu.child(tıklanansatırkullaniciID).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.hasChild("resim"))
                       {
                           //verileri firebaseden çekme
                           String profilResmin=snapshot.child("resim").getValue().toString();
                           String kullanicininadi=snapshot.child("ad").getValue().toString();
                           String kullanicidurumu=snapshot.child("durum").getValue().toString();

                           //holdera veri aktarımı
                           holder.kullaniciadi.setText(kullanicininadi);
                           holder.kullanicinindurumu.setText(kullanicidurumu);
                           Picasso.get().load(profilResmin).placeholder(R.drawable.icons8).into(holder.profilresmi);
                       }
                       else
                       {
                           //verileri firebaseden çekme

                           String kullanicininadi=snapshot.child("ad").getValue().toString();
                           String kullanicidurumu=snapshot.child("durum").getValue().toString();

                           //holdera veri aktarımı
                           holder.kullaniciadi.setText(kullanicininadi);
                           holder.kullanicinindurumu.setText(kullanicidurumu);

                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
            }

            @NonNull
            @Override
            public KisilerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanicigosterme,parent,false);

                KisilerViewholder viewemholder=new KisilerViewholder(view);
                return viewemholder;
            }
        };
        kisilerlistem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class KisilerViewholder extends RecyclerView.ViewHolder{

        TextView kullaniciadi,kullanicinindurumu;
        CircleImageView profilresmi;

        public KisilerViewholder(@NonNull View itemView) {
            super(itemView);
            kullaniciadi=itemView.findViewById(R.id.kullaniciAdi);
            kullanicinindurumu=itemView.findViewById(R.id.kullanicidurumu);
            profilresmi=itemView.findViewById(R.id.kullaniciresmi);



        }
    }

}