package com.lovelycoding.whatapp.adapter.privatechat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView tvSenderMessage,tvSenderMessageTime,tvReceiverMessage,tvReceiverMessageTime;
    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSenderMessage = itemView.findViewById(R.id.tv_message_sender);
        tvSenderMessageTime = itemView.findViewById(R.id.tv_message_sender_time);
        tvReceiverMessage = itemView.findViewById(R.id.tv_message_receiver);
        tvReceiverMessageTime = itemView.findViewById(R.id.tv_message_receiver_time);
    }
}
