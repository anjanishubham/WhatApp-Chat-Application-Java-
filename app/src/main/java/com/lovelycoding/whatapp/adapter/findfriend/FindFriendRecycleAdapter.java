package com.lovelycoding.whatapp.adapter.findfriend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FindFriendRecycleAdapter extends RecyclerView.Adapter {
    private static final String TAG = "FindFriendRecycleAdapte";

    Context context;
    private OnclickFindFriend mListener;
    List<Contact> mContactList = new ArrayList<>();

    public FindFriendRecycleAdapter(OnclickFindFriend mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_display, parent, false);
        return new FindFriendViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ((FindFriendViewHolder) holder).tvUserStatus.setText(mContactList.get(position).getStatus());
        ((FindFriendViewHolder) holder).tvUserName.setText(mContactList.get(position).getName());

        // String child_url = "https://www.michaelkormos.com/wp-content/uploads/2019/02/12-7126-pp_gallery/boy-crawling-in-grass-1024x741.jpg";
        if (!mContactList.get(position).getImage().isEmpty())
            Picasso.with(context.getApplicationContext())
                    .load(mContactList.get(position).getImage())
                    .placeholder(R.drawable.profile_image)
                    .error(R.drawable.login_photo)
                    .into(((FindFriendViewHolder) holder).civUserImage);


        Log.d(TAG, "onBindViewHolder: view is binded  " + mContactList.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mContactList.size());
        if (mContactList == null)
            return 0;
        else
            return mContactList.size();
    }

    public void setContactList(List<Contact> mContactList) {
        this.mContactList = mContactList;
    }
}
