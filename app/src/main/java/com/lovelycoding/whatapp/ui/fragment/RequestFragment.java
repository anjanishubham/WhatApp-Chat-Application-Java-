package com.lovelycoding.whatapp.ui.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.findfriend.FindFriendRecycleAdapter;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.viewmodel.RequestFragmentViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {

    private static final String TAG = "RequestFragment";
    private RecyclerView rvRequestFragment;
    private TextView emptyView;// this view is visiable only if when recycle view is empty
    private ProgressBar mProgressBar;


    private DatabaseReference chatRequestDatabaseRef,userDatabaseRef,contactDatabaseRef;
    private FirebaseAuth mAuth;
    static String currentUserId,sendUserId;
    private boolean isRequestEmpty=false;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request, container, false);
        rvRequestFragment=view.findViewById(R.id.user_request_fragment_rv);
        emptyView=view.findViewById(R.id.emptyView);
        emptyView.setText("No new chat request !");
        mProgressBar=view.findViewById(R.id.progressbar_request_fragment);
        mAuth=FirebaseAuth.getInstance();
        chatRequestDatabaseRef= FirebaseDatabase.getInstance().getReference().child("chat request");
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        contactDatabaseRef= FirebaseDatabase.getInstance().getReference().child("contacts");
        currentUserId=mAuth.getCurrentUser().getUid();
        rvRequestFragment.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        isRequestEmpty=false;
        super.onStart();
        mProgressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerOptions<Contact> options=new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(chatRequestDatabaseRef.child(currentUserId),Contact.class).build();

        FirebaseRecyclerAdapter<Contact,RequestViewHolder> adapter=new FirebaseRecyclerAdapter<Contact, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contact contact) {
                final String senderUserRequestId=getRef(position).getKey();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String type="";
                       if(dataSnapshot.getValue()!=null)
                        type =dataSnapshot.getValue().toString();
                        if (type.equals("receive"))
                        {
                            userDatabaseRef.child(senderUserRequestId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    isRequestEmpty=true;
                                    emptyView.setVisibility(View.GONE);
                                    mProgressBar.setVisibility(View.GONE);
                                    holder.constraintLayout.setVisibility(View.VISIBLE);
                                    holder.tvAcceptRequest.setVisibility(View.VISIBLE);
                                    holder.tvDeclinedRequest.setVisibility(View.VISIBLE);
                                    final Contact contact1=dataSnapshot.getValue(Contact.class);
                                    Log.d(TAG, "onDataChange: "+contact1.toString());
                                    holder.tvUserName.setText(contact1.getName());
                                    holder.tvUserStatus.setText(contact1.getStatus());
                                    sendUserId=contact1.getUid();

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            holder.setIsRecyclable(true);
                            isRequestEmpty=false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(!isRequestEmpty)
                {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_chat_display_list,parent,false);
                return new RequestViewHolder(view);

            }
        };
        rvRequestFragment.setAdapter(adapter);
        adapter.startListening();

    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView civUserImage;
        private ConstraintLayout constraintLayout;
        private TextView tvUserName,tvUserStatus,tvAcceptRequest,tvDeclinedRequest;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            civUserImage=itemView.findViewById(R.id.user_request_civ);
            tvUserName=itemView.findViewById(R.id.tv_user_request_user_name);
            tvUserStatus=itemView.findViewById(R.id.tv_user_request_user_status);
            tvAcceptRequest=itemView.findViewById(R.id.bt_user_request_accept);
            tvDeclinedRequest=itemView.findViewById(R.id.bt_user_request_cancel);
            constraintLayout=itemView.findViewById(R.id.layout_constraint);

            tvDeclinedRequest.setOnClickListener(this);
            tvAcceptRequest.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            final DatabaseReference chatRequestDatabaseRef= FirebaseDatabase.getInstance().getReference().child("chat request");
            final DatabaseReference contactDatabaseRef= FirebaseDatabase.getInstance().getReference().child("contacts");


            switch (view.getId())
            {

                case R.id.bt_user_request_accept:
                {
                    Log.d(TAG, "onClick: acccept the chat request");
                    contactDatabaseRef.child(sendUserId).
                            child(currentUserId).
                            child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                contactDatabaseRef.child(currentUserId).
                                        child(sendUserId).
                                        child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            chatRequestDatabaseRef.child(currentUserId).
                                                    child(sendUserId).
                                                    child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });

                    break;
                }
                case R.id.bt_user_request_cancel:
                {

                    Log.d(TAG, "onClick: "+sendUserId +" current user Id::"+ currentUserId+" dataref"+chatRequestDatabaseRef);

                    chatRequestDatabaseRef.child(sendUserId).
                            child(currentUserId).
                            child("request_type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                chatRequestDatabaseRef.child(currentUserId).
                                        child(sendUserId).
                                        child("request_type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {

                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        }
    }
}
