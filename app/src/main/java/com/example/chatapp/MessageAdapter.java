package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    public MessageAdapter() {
    }

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<MessageModel> messageModelList;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messageModelList = new ArrayList<>();
    }

    public void add(MessageModel messageModel) {
        messageModelList.add(messageModel);
        notifyItemInserted(messageModelList.size() - 1);
    }

    public void addAll(List<MessageModel> messages) {
        int initialSize = messageModelList.size();
        messageModelList.addAll(messages);
        notifyItemRangeInserted(initialSize, messages.size());
    }

    public void clear() {
        messageModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SENT) {
            View view = inflater.inflate(R.layout.messagerowsent, parent, false);
            return new MyViewHolder(view, VIEW_TYPE_SENT);
        } else {
            View view = inflater.inflate(R.layout.messagerowrecieve, parent, false);
            return new MyViewHolder(view, VIEW_TYPE_RECEIVED);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            holder.bindSentMessage(messageModel.getMessage());
        } else {
            holder.bindReceivedMessage(messageModel.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModelList.get(position).getSenderid().equals(FirebaseAuth.getInstance().getUid())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public List<MessageModel> getMessageModelList() {
        return messageModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textSendMessage;
        TextView textReceiveMessage;

        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            if (viewType == VIEW_TYPE_SENT) {
                textSendMessage = itemView.findViewById(R.id.textviewsentmessage);
            } else {
                textReceiveMessage = itemView.findViewById(R.id.textviewrecievemessage);
            }
        }

        public void bindSentMessage(String message) {
            if (textSendMessage != null) {
                textSendMessage.setText(message);
            }
        }

        public void bindReceivedMessage(String message) {
            if (textReceiveMessage != null) {
                textReceiveMessage.setText(message);
            }
        }
    }
}
