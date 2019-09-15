package com.lovelycoding.whatapp.adapter.findfriend;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "FindFriendViewHolder";
    private OnclickFindFriend mListener;
    TextView tvUserName,tvUserStatus;
   CircleImageView civUserImage,civUserOnline;
//    AppCompatImageView civUserImage;

    public FindFriendViewHolder(@NonNull View itemView,OnclickFindFriend mListener) {
        super(itemView);
        this.mListener=mListener;
        tvUserName = itemView.findViewById(R.id.tv_user_layout_user_name);
        tvUserStatus = itemView.findViewById(R.id.tv_user_layout_user_status);
        civUserImage=itemView.findViewById(R.id.user_layout_civ);
        civUserOnline=itemView.findViewById(R.id.civ_find_friend_is_user_online);
        Log.d(TAG, "FindFriendViewHolder: View is created");
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {

        mListener.onClickToFindFriend(getAdapterPosition());
    }
}
