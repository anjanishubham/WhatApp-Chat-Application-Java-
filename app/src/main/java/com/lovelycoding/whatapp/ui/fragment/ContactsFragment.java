package com.lovelycoding.whatapp.ui.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.findfriend.FindFriendRecycleAdapter;
import com.lovelycoding.whatapp.adapter.findfriend.OnclickFindFriend;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.repository.Repository;
import com.lovelycoding.whatapp.viewmodel.ContactActivtyViewModel;
import com.lovelycoding.whatapp.viewmodel.ContactFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment implements OnclickFindFriend {
    private static final String TAG = "ContactsFragment";
    ContactFragmentViewModel mViewModel;
    private RecyclerView recyclerView;
    private List<Contact> mContactList = new ArrayList<>();
    private FindFriendRecycleAdapter mAdapter;
    private ProgressBar mProgressBar;

    private boolean isContactListEmpty=false;

    public ContactsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView=view.findViewById(R.id.contact_list_rv);
        mProgressBar=view.findViewById(R.id.progressbar__contact_fragment);

        mViewModel= ViewModelProviders.of(this).get(ContactFragmentViewModel.class);
        mAdapter=new FindFriendRecycleAdapter(this);

        contactListObserver();
      //  Repository.getRepositoryInstance().getContactList(contactDatabaseRef,currentUserId);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecycleView();

    }

    private void initRecycleView() {
        Log.d(TAG, "onChanged: "+mContactList);

        mAdapter.setContactList(mContactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(mAdapter);

    }

    private void contactListObserver() {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewModel.getUserList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {

                mContactList.clear();

                if(contacts.size()!=0)
                {
                    mContactList.addAll(contacts);
                    Log.d(TAG, "onChanged: "+mContactList);
                    mProgressBar.setVisibility(View.GONE);
                    isContactListEmpty=false;
                }

                if(!isContactListEmpty)
                {
                    mProgressBar.setVisibility(View.GONE);
                }
                mAdapter.setContactList(mContactList);
                mAdapter.notifyDataSetChanged();


            }
        });
    }

    @Override
    public void onClickToFindFriend(int position) {

    }
}
