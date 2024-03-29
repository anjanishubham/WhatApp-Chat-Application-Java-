package com.lovelycoding.whatapp.ui.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.findfriend.FindFriendViewHolder;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.ui.activity.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    View chatFragmentView;
    RecyclerView chatFragmentRecycleView;
    TextView emptyChat;
    private ProgressBar mProgressBar;

    private DatabaseReference chatDatabaseRef,userDatabaseRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    public static boolean isChatEmpty=false;
    Map<String, Bitmap> bitMap = new HashMap<>();


    public ChatsFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatFragmentView= inflater.inflate(R.layout.fragment_chats, container, false);
        emptyChat = chatFragmentView.findViewById(R.id.emptyView);
        emptyChat.setText("Chat list empty !");
        mProgressBar=chatFragmentView.findViewById(R.id.progressbar_chat_fragment);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        chatDatabaseRef= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserId);
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatFragmentRecycleView=chatFragmentView.findViewById(R.id.rv_chat_fragment);
        chatFragmentRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        return chatFragmentView;
    }

    @Override
    public void onStart() {
        mProgressBar.setVisibility(View.VISIBLE);
        super.onStart();
        FirebaseRecyclerOptions<Contact>  options=new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(chatDatabaseRef,Contact.class).build();

        FirebaseRecyclerAdapter<Contact,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Contact, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int i, @NonNull Contact contact) {

                final String userId= getRef(i).getKey();
                userDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProgressBar.setVisibility(View.GONE);

                        final Contact c=dataSnapshot.getValue(Contact.class);
                        if(c!=null)
                        {
                            isChatEmpty=true;
                            emptyChat.setVisibility(View.GONE);
                            holder.constraintLayout.setVisibility(View.VISIBLE);
                            Log.d(TAG, "getUser : "+c.toString());
                            holder.tvUsername.setText(c.getName());
                            holder.tvUserStatus.setText(c.getLast_online_date()+" "+c.getLast_online_time());

                            if(bitMap.containsKey(c.getUid())) {
                                holder.civProfileImage.setImageBitmap(bitMap.get(c.getUid()));
                            }
                            else
                                downloadProfileImage(holder, c.getUid());


                            //
                            //Glide.with(getContext()).load(c.getImage().placeholder(R.drawable.profile_image).into((holder).civProfileImage);
                            //Glide.with(getContext().getApplicationContext()).load(c.getImage()).apply(RequestOptions.circleCropTransform()).into(holder.civProfileImage);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatActivityIntent=new Intent(getContext(), ChatActivity.class);
                                    chatActivityIntent.putExtra("user",c);
                                    Log.d(TAG, "onClick: "+c.toString());
                                    startActivity(chatActivityIntent);
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(getContext()).inflate(R.layout.layout_user_chat_display_list,parent,false);


                return(new ChatViewHolder(view));
            }

        };

        if(!isChatEmpty)
        {
            mProgressBar.setVisibility(View.GONE);
            emptyChat.setVisibility(View.VISIBLE);
        }
        chatFragmentRecycleView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        private CircleImageView civProfileImage;
        TextView tvUsername,tvUserStatus;
        public ChatViewHolder(@NonNull View itemView){
            super(itemView);

            civProfileImage=itemView.findViewById(R.id.user_request_civ);
            tvUsername=itemView.findViewById(R.id.tv_user_request_user_name);
            tvUserStatus=itemView.findViewById(R.id.tv_user_request_user_status);
            constraintLayout=itemView.findViewById(R.id.layout_constraint);

        }
    }

    public void downloadProfileImage(final ChatViewHolder holder, final String fileName)
    {
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference reference= FirebaseStorage.getInstance().getReference().child("Profile Image");
        Log.d(TAG, "downloadProfileImage: "+fileName+".PNG");
        Log.d(TAG, "downloadProfileImage: "+reference);
        reference.child(fileName+".PNG").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.civProfileImage.setImageBitmap(bitmap);
                bitMap.put(fileName,bitmap);
                Log.d(TAG, "onPostExecute: "+bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}
