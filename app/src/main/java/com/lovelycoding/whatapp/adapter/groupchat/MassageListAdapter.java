package com.lovelycoding.whatapp.adapter.groupchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.GroupChatModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MassageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<GroupChatModel> mList = new ArrayList<>();
    private String currentUserName;


    public void setMessageList(List<GroupChatModel> list) {
        mList=list;
    }
    public void setCurrentUserName(String currentUserName)
    {
        this.currentUserName=currentUserName;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_send, parent, false);
            return new MassageSendHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_recieved, parent, false);
            return new RecivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case VIEW_TYPE_MESSAGE_SENT:
            {
                ((MassageSendHolder)holder).tvMessageBody.setText(mList.get(position).getUserMassage());
                ((MassageSendHolder)holder).tvMessageTime.setText(mList.get(position).getMessageTime());
                break;
            }
            case VIEW_TYPE_MESSAGE_RECEIVED:
            {
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat currentDateFormat=new SimpleDateFormat("MM-dd-yyyy");
                String currentDate=currentDateFormat.format(calendar.getTime());
                ((RecivedMessageHolder)holder).messageText.setText(mList.get(position).getUserMassage());
                ((RecivedMessageHolder)holder).nameText.setText(mList.get(position).getUserName());
                if(!currentDate.trim().equals(mList.get(position).getMessageDate().trim()))
                ((RecivedMessageHolder)holder).timeText.setText(mList.get(position).getMessageTime()+" "+ mList.get(position).getMessageDate());
              else {
                    ((RecivedMessageHolder)holder).timeText.setText(mList.get(position).getMessageTime());
                }
                //((RecivedMessageHolder)holder).messageText.setText(mList.get(position).getUserMassage());
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mList!=null)
        return mList.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
       // return super.getItemViewType(position);
        if(mList.get(position).getUserName().equals(currentUserName))
        {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else
        {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
}
