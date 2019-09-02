package com.lovelycoding.whatapp.adapter.chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecivedMessageHolder extends RecyclerView.ViewHolder {

    TextView messageText, timeText, nameText;
    CircleImageView profileImage;

    RecivedMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage =  itemView.findViewById(R.id.image_message_profile);
    }
}
