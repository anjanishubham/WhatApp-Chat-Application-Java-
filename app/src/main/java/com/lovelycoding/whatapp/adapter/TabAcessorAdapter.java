package com.lovelycoding.whatapp.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lovelycoding.whatapp.ui.fragment.ChatsFragment;
import com.lovelycoding.whatapp.ui.fragment.ContactsFragment;
import com.lovelycoding.whatapp.ui.fragment.GroupsFragment;
import com.lovelycoding.whatapp.ui.fragment.RequestFragment;

public class TabAcessorAdapter extends FragmentPagerAdapter {
    public TabAcessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
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
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Request";
            default:
                return null;
        }
    }
}
