
package com.example.munsan.l;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessagesText,receiveMesageText, timereceiver, timesender, userreceive;
        public RelativeLayout sender,receiver;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessagesText = (TextView) itemView.findViewById(R.id.send_message_text);
            receiveMesageText = (TextView) itemView.findViewById(R.id.receive_message_text);
            sender = (RelativeLayout)itemView.findViewById(R.id.sender);
            receiver = (RelativeLayout)itemView.findViewById(R.id.receiver);

            userreceive = (TextView)itemView.findViewById(R.id.usernametv);
            timereceiver = (TextView)itemView.findViewById(R.id.time);
            timesender = (TextView)itemView.findViewById(R.id.time2);


        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(i);

        String username = messages.getUsername();
        String message = messages.getMessage();
        String date = messages.getDate();
        String time = messages.getTime();
        String uid = messages.getUserID();

        if (uid.equals(currentUserId)){
            messageViewHolder.receiveMesageText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiver.setVisibility(View.INVISIBLE);
            messageViewHolder.receiveMesageText.setVisibility(View.INVISIBLE);
            messageViewHolder.timereceiver.setVisibility(View.INVISIBLE);
            messageViewHolder.userreceive.setVisibility(View.INVISIBLE);


            messageViewHolder.senderMessagesText.setVisibility(View.VISIBLE);
            messageViewHolder.sender.setVisibility(View.VISIBLE);
            messageViewHolder.timesender.setVisibility(View.VISIBLE);


            messageViewHolder.sender.setBackgroundResource(R.drawable.send_messages_layout);
            messageViewHolder.senderMessagesText.setText(messages.getMessage());
            messageViewHolder.timesender.setText(messages.getTime());
        }else{

            messageViewHolder.receiveMesageText.setVisibility(View.VISIBLE);
            messageViewHolder.receiver.setVisibility(View.VISIBLE);
            messageViewHolder.receiveMesageText.setVisibility(View.VISIBLE);
            messageViewHolder.timereceiver.setVisibility(View.VISIBLE);
            messageViewHolder.userreceive.setVisibility(View.VISIBLE);


            messageViewHolder.senderMessagesText.setVisibility(View.INVISIBLE);
            messageViewHolder.sender.setVisibility(View.INVISIBLE);
            messageViewHolder.timesender.setVisibility(View.INVISIBLE);

            messageViewHolder.receiver.setBackgroundResource(R.drawable.receive_messages_layout);
            messageViewHolder.receiveMesageText.setText(messages.getMessage());
            messageViewHolder.timereceiver.setText(messages.getTime());
            messageViewHolder.userreceive.setText(messages.getUsername());
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

}