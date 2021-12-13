package adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mee.msjgrp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import model.mesajlarmodeli;

public class MesajAdaptor extends RecyclerView.Adapter<MesajAdaptor.MesajlarViewholder>
{
    private List<mesajlarmodeli> kullanicimesajlarlistesi;
     private FirebaseAuth mYetki;
     private DatabaseReference kullanicilaryolu;


    public MesajAdaptor(List<mesajlarmodeli> kullanicimesajlarlistesi)
    {
        this.kullanicimesajlarlistesi=kullanicimesajlarlistesi;
    }
    public class MesajlarViewholder extends RecyclerView.ViewHolder
    {
        public TextView gonderenmesajmetni,alicimesajmetni;
        public CircleImageView aliciprofilresmi;

        public MesajlarViewholder(View itemView)
        {
            super(itemView);

            alicimesajmetni=itemView.findViewById(R.id.mesajalanchat1);
            gonderenmesajmetni=itemView.findViewById(R.id.mesajgonderenchat2);
            aliciprofilresmi=itemView.findViewById(R.id.mesajprofilresmi);
        }
    }

    @NonNull
    @Override
    public MesajlarViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ozel_mesajlar_layaout,parent,false);

         //firebase tanımlama
        mYetki=FirebaseAuth.getInstance();
        return new MesajlarViewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MesajlarViewholder holder, int position)
    {
      String mesajgonderenId=mYetki.getCurrentUser().getUid();
        mesajlarmodeli mesajlar=kullanicimesajlarlistesi.get(position);

        String kimdenkullaniciId=mesajlar.getKimden();
        String kimdenmesajturu=mesajlar.getTur();

        kullanicilaryolu= FirebaseDatabase.getInstance().getReference().child("kullanicilar").child(kimdenkullaniciId);
        kullanicilaryolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
               if (snapshot.hasChild("resim"))
               {
                   String resmialici=snapshot.child("resim").getValue().toString();

                   Picasso.get().load(resmialici).placeholder(R.drawable.icons8).into(holder.aliciprofilresmi);


               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });



        if (kimdenmesajturu.equals("metin"))
        {
            holder.alicimesajmetni.setVisibility(View.INVISIBLE);
            holder.aliciprofilresmi.setVisibility(View.INVISIBLE);

            if (kimdenkullaniciId.equals(mesajgonderenId))
            {

                holder.gonderenmesajmetni.setBackgroundResource(R.drawable.gonderen_mesajlari_layout);
                holder.gonderenmesajmetni.setTextColor(Color.BLACK);
                holder.gonderenmesajmetni.setText(mesajlar.getMesaj());
            }
            else
            {
                holder.gonderenmesajmetni.setVisibility(View.INVISIBLE);

                holder.aliciprofilresmi.setVisibility(View.VISIBLE);
                holder.alicimesajmetni.setVisibility(View.VISIBLE);

                holder.alicimesajmetni.setBackgroundResource(R.drawable.alici_mesajlari_dosyasi);
                holder.alicimesajmetni.setTextColor(Color.BLACK);
                holder.alicimesajmetni.setText(mesajlar.getMesaj());
            }
        }

    }

    @Override
    public int getItemCount() {
        return kullanicimesajlarlistesi.size();
    }


}
