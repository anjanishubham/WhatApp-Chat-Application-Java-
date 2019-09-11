package com.lovelycoding.whatapp.adapter.privatechat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.PrivateChatMessage;
import com.lovelycoding.whatapp.util.Util;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter
{
    private static final String TAG = "MessageAdapter";
    List<PrivateChatMessage> messageList ;

    public MessageAdapter() {
        messageList=new ArrayList<>();
    }

    FirebaseAuth mAuth;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth=FirebaseAuth.getInstance();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_chat_activity_rv_list_item,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String senderUserId=mAuth.getCurrentUser().getUid();
        String time= Util.dateConversion(messageList.get(position).getTimeStamp());
        if(messageList.get(position).getMessageFrom().equals(senderUserId))
        {
            ((MessageViewHolder)holder).tvSenderMessage.setVisibility(View.VISIBLE);
            ((MessageViewHolder)holder).tvSenderMessageTime.setVisibility(View.VISIBLE);
            ((MessageViewHolder)holder).tvSenderMessage.setText(messageList.get(position).getMessage());
            ((MessageViewHolder)holder).tvSenderMessageTime.setText(time);
            ((MessageViewHolder)holder).tvReceiverMessage.setVisibility(View.INVISIBLE);
            ((MessageViewHolder)holder).tvReceiverMessageTime.setVisibility(View.INVISIBLE);

        }
        else {
            ((MessageViewHolder)holder).tvSenderMessage.setVisibility(View.INVISIBLE);
            ((MessageViewHolder)holder).tvSenderMessageTime.setVisibility(View.INVISIBLE);
            ((MessageViewHolder)holder).tvReceiverMessage.setVisibility(View.VISIBLE);
            ((MessageViewHolder)holder).tvReceiverMessageTime.setVisibility(View.VISIBLE);
            ((MessageViewHolder)holder).tvReceiverMessage.setText(messageList.get(position).getMessage());
            ((MessageViewHolder)holder).tvReceiverMessageTime.setText(time);
        }

    }
    public void setMessageListValue(List<PrivateChatMessage> list)
    {
        Log.d(TAG, "getItemCount: "+messageList.size());

        this.messageList=list;

    }

    @Override
    public int getItemCount() {
        if(messageList!=null)
         return messageList.size();
        else return 0;
    }
}