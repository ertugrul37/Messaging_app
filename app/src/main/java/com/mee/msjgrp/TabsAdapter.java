package com.mee.msjgrp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAdapter extends FragmentPagerAdapter {
    public TabsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }
    Strings metin = new Strings();

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestFragment requestFragment= new RequestFragment();
                return requestFragment;
            case 1:
                ChatsFragment chatsFragment= new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment= new FriendsFragment();
                return friendsFragment;
            case 3:
                TaleplerFragment taleplerFragment= new TaleplerFragment();
                return taleplerFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return metin.aj;
            case 1:
                return metin.ak;
            case 2:
                return metin.al;
            case 3:
                return metin.am;
            default:
                return null;
        }

    }
}
