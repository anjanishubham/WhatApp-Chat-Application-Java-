package com.lovelycoding.whatapp.adapter.findfriend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.Contact;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendRecycleAdapter extends RecyclerView.Adapter {
    private static final String TAG = "FindFriendRecycleAdapte";

    Context context;
    private OnclickFindFriend mListener;
    List<Contact> mContactList = new ArrayList<>();
    public Map<String, Bitmap> bitMap = new HashMap<>();

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

        if(mContactList.get(position).getLogin_state().equals("online"))
            ((FindFriendViewHolder) holder).civUserOnline.setVisibility(View.VISIBLE);



        /*if(!mContactList.get(position).getImage().isEmpty())
        {
            if(bitMap.containsKey(mContactList.get(position).getImage()))
                ((FindFriendViewHolder)holder).civUserImage.setImageBitmap(bitMap.get(mContactList.get(position).getImage()));
            else {
                DownloadImage downloadImage = new DownloadImage();

                downloadImage.setViewHolder(holder);
                downloadImage.execute(mContactList.get(position).getImage());
            }
        }*/

        if(!mContactList.get(position).getUid().isEmpty()){
            Log.d(TAG, "onBindViewHolder: "+mContactList.get(position).getImage());
            if(bitMap.containsKey(mContactList.get(position).getUid()))
                ((FindFriendViewHolder)holder).civUserImage.setImageBitmap(bitMap.get(mContactList.get(position).getUid()));
           else
               downloadProfileImage(holder,mContactList.get(position).getUid());
        }
        else
            ((FindFriendViewHolder)holder).civUserImage.setVisibility(View.VISIBLE);



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

    public void downloadProfileImage(final RecyclerView.ViewHolder holder, final String fileName)
    {
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference reference= FirebaseStorage.getInstance().getReference().child("Profile Image");
        Log.d(TAG, "downloadProfileImage: "+fileName+".PNG");
        Log.d(TAG, "downloadProfileImage: "+reference);
        reference.child(fileName+".PNG").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                ((FindFriendViewHolder)holder).civUserImage.setImageBitmap(bitmap);
                bitMap.put(fileName,bitmap);
                Log.d(TAG, "onPostExecute: "+bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    private class DownloadImage extends AsyncTask<String,Void, Bitmap> {

        private RecyclerView.ViewHolder holder;
        private String urlString;
        public void setViewHolder(RecyclerView.ViewHolder holder){
            this.holder=holder;
        }
        @Override
        protected Bitmap doInBackground(String... imageurl) {

             urlString=imageurl[0];
            Log.d(TAG, "doInBackground: "+urlString);
            InputStream inputStream=null;
            Bitmap bitmap=null;

            try {
                URL imageUrl=new URL(urlString);
                inputStream= (InputStream) imageUrl.getContent();
                bitmap= BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finally {
                if(inputStream!=null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            ((FindFriendViewHolder)holder).civUserImage.setImageBitmap(bitmap);
             bitMap.put(urlString,bitmap);
            Log.d(TAG, "onPostExecute: "+bitmap);

        }
    }
}
