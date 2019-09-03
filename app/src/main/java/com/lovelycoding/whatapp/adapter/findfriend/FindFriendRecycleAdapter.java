package com.lovelycoding.whatapp.adapter.findfriend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class FindFriendRecycleAdapter extends RecyclerView.Adapter {
    private static final String TAG = "FindFriendRecycleAdapte";

    Context context;
    private OnclickFindFriend mListener;
    List<Contact> mContactList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        this.context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_display,parent,false);
        return new FindFriendViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        ((FindFriendViewHolder)holder).tvUserStatus.setText(mContactList.get(position).getUserStatus());
        ((FindFriendViewHolder)holder).tvUserName.setText(mContactList.get(position).getUserName());
        if(!mContactList.get(position).getUserProfileUrl().isEmpty())
            Glide.with(context).load(mContactList.get(position).getUserProfileUrl()).into(((FindFriendViewHolder)holder).civUserImage);
        else
        Glide.with(context).load(mContactList.get(position).getUserProfileUrl()).placeholder(R.drawable.profile_image).into(((FindFriendViewHolder)holder).civUserImage);
        Log.d(TAG, "onBindViewHolder: view is biinded  "+mContactList.get(position).getUserProfileUrl());
     }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+mContactList.size());
        if(mContactList==null)
        return 0;
        else
            return mContactList.size();
    }

    public void setContactList(List<Contact> mContactList) {
        this.mContactList=mContactList;
    }
}
