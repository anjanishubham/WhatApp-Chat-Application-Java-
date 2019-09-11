package com.lovelycoding.whatapp.adapter.groupchat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

public class MassageSendHolder extends RecyclerView.ViewHolder {

    TextView tvMessageBody,tvMessageTime;
    public MassageSendHolder(@NonNull View itemView) {
        super(itemView);
        tvMessageBody=itemView.findViewById(R.id.text_message_body);
        tvMessageTime=itemView.findViewById(R.id.text_message_time);

    }
}
