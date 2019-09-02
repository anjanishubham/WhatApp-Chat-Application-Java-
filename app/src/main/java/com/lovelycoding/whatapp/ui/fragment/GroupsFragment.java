package com.lovelycoding.whatapp.ui.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.groupchatlist.GroupChatAdapter;
import com.lovelycoding.whatapp.adapter.groupchatlist.GroupChatClickListener;
import com.lovelycoding.whatapp.repository.Repository;
import com.lovelycoding.whatapp.ui.activity.GroupChatActivity;
import com.lovelycoding.whatapp.viewmodel.GroupViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment implements GroupChatClickListener {
    private static final String TAG = "GroupsFragment";

    RecyclerView recyclerView;
    GroupChatAdapter mAdapter;
    List<String> chatList = new ArrayList<>();
    Repository mRepo;
    GroupViewModel mGroupViewModel;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_groups, container, false);

        initRecycleView(view);
        return view;
    }

    private void initRecycleView(View view) {
        recyclerView=view.findViewById(R.id.group_rv);

        mAdapter=new GroupChatAdapter(this);
        mRepo=new Repository();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        // RecycleViewItemDecorator decorator = new RecycleViewItemDecorator(15);
        //mCartRecyclerView.addItemDecoration(decorator);
        mAdapter.setGroupChatList(chatList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mGroupViewModel=ViewModelProviders.of(getActivity()).get(GroupViewModel.class);
        initObservers();

    }

    private void initObservers() {
        mRepo.getGroupList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
             

               if(list==null)
               {
                   Log.d(TAG, "onChanged: empty list");
                  // Log.d(TAG, "onChanged: "+chatList.size());
               }
               else if(list.size()>0)
                {
                    chatList.clear();
                    chatList.addAll(list);
                    Log.d(TAG, "onChanged: "+chatList.size());
                }
               mAdapter.setGroupChatList(chatList);
               mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void getCurrentPosition(int position) {

        Intent intent=new Intent(getContext(), GroupChatActivity.class);
        intent.putExtra("currentGroupName",chatList.get(position));

        startActivity(intent);

    }
}
