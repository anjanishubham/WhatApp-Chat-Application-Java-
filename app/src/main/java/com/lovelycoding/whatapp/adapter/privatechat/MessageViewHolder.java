package com.lovelycoding.whatapp.adapter.privatechat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnClickFlieSharing mOnclickListener;
    TextView tvSenderMessage,tvSenderMessageTime,tvReceiverMessage,tvReceiverMessageTime;
    public MessageViewHolder(@NonNull View itemView, OnClickFlieSharing mOnclickListener) {
        super(itemView);
        this.mOnclickListener=mOnclickListener;
        tvSenderMessage = itemView.findViewById(R.id.tv_message_sender);
        tvSenderMessageTime = itemView.findViewById(R.id.tv_message_sender_time);
        tvReceiverMessage = itemView.findViewById(R.id.tv_message_receiver);
        tvReceiverMessageTime = itemView.findViewById(R.id.tv_message_receiver_time);
    }
    @Override
    public void onClick(View view) {

        mOnclickListener.onClickShareFile();
    }
}
