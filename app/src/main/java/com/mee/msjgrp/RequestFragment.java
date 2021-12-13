package com.mee.msjgrp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    private View grupCerceveView;
    private ListView list_view;
    private ArrayAdapter <String>arrayAdapter;
    private ArrayList<String>grup_listeleri=new ArrayList<>();

    //Fİrebase
    private DatabaseReference grupyolu;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        grupCerceveView = inflater.inflate(R.layout.fragment_request, container, false);

        ///FİREBASE

         grupyolu= FirebaseDatabase.getInstance().getReference().child("Grublar");

        //TANIMLAMALAR

        list_view=grupCerceveView.findViewById(R.id.list_viewgruplar);
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,grup_listeleri);
        list_view.setAdapter(arrayAdapter);

    //GRUPLARI ALMA KODLARI
        gruplarıalvegoster();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mevcutgrupadi=adapterView.getItemAtPosition(i).toString();

                Intent grupChatActivty=new Intent(getContext(),grupchatactivity.class);
                grupChatActivty.putExtra("grupAdı",mevcutgrupadi);
                startActivity(grupChatActivty);
            }
        });

        return grupCerceveView;
    }

    private void gruplarıalvegoster() {



      grupyolu.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              Set<String> set=new HashSet<>();
              Iterator iterator= snapshot.getChildren().iterator();
              while(iterator.hasNext())
              {
                  set.add(((DataSnapshot)iterator.next()).getKey());
              }
              grup_listeleri.clear();
              grup_listeleri.addAll(set);
              arrayAdapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });

    }
}