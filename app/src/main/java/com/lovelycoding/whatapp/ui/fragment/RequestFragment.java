package com.lovelycoding.whatapp.ui.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


    private DatabaseReference chatRequestDatabaseRef,userDatabaseRef;
    private FirebaseAuth mAuth;
    String currentUserId,sendUserId;

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

        mAuth=FirebaseAuth.getInstance();
        chatRequestDatabaseRef= FirebaseDatabase.getInstance().getReference().child("chat request");
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
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
        super.onStart();

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

                        String type=dataSnapshot.getValue().toString();
                        if (type.equals("receive"))
                        {
                            userDatabaseRef.child(senderUserRequestId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    emptyView.setVisibility(View.GONE);
                                    holder.cardView.setVisibility(View.VISIBLE);
                                    holder.tvAcceptRequest.setVisibility(View.VISIBLE);
                                    holder.tvDeclinedRequest.setVisibility(View.VISIBLE);
                                    final Contact contact1=dataSnapshot.getValue(Contact.class);
                                    Log.d(TAG, "onDataChange: "+contact1.toString());
                                    holder.tvUserName.setText(contact1.getName());
                                    holder.tvUserStatus.setText(contact1.getStatus());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
        private CardView cardView;
        private TextView tvUserName,tvUserStatus,tvAcceptRequest,tvDeclinedRequest;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            civUserImage=itemView.findViewById(R.id.user_request_civ);
            tvUserName=itemView.findViewById(R.id.tv_user_request_user_name);
            tvUserStatus=itemView.findViewById(R.id.tv_user_request_user_status);
            tvAcceptRequest=itemView.findViewById(R.id.bt_user_request_accept);
            tvDeclinedRequest=itemView.findViewById(R.id.bt_user_request_cancel);
            cardView=itemView.findViewById(R.id.cardView);

            tvDeclinedRequest.setOnClickListener(this);
            tvAcceptRequest.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
