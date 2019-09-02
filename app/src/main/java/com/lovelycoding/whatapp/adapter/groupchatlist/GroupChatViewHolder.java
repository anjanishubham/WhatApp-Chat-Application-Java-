package com.lovelycoding.whatapp.adapter.groupchatlist;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

    CircleImageView groupImage;
    AppCompatTextView tvGroupName;

    GroupChatClickListener mListener;
    public GroupChatViewHolder(@NonNull View itemView,GroupChatClickListener mListener) {
        super(itemView);
        this.mListener=mListener;
        groupImage=itemView.findViewById(R.id.group_image);
        tvGroupName=itemView.findViewById(R.id.tv_group_name);
        itemView.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        mListener.getCurrentPosition(getAdapterPosition());
    }
}
