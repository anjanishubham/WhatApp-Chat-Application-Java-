package com.lovelycoding.whatapp.adapter.groupchatlist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GroupChatAdapter extends RecyclerView.Adapter {
    GroupChatClickListener mListener;
    public GroupChatAdapter(GroupChatClickListener mListener) {
       this.mListener=mListener;
    }

    List<String> groupChatList=new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_item_view,parent,false);

        return new GroupChatViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((GroupChatViewHolder)holder).tvGroupName.setText(groupChatList.get(position));
        Log.d(TAG, "onBindViewHolder: "+groupChatList.get(position));

    }

    public void setGroupChatList(List<String> groupChatList) {
        this.groupChatList=groupChatList;
    }
    @Override
    public int getItemCount() {
        if(groupChatList!=null)
        return groupChatList.size();
        else
            return 0;
    }
}
