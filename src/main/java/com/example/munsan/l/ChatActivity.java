package com.example.munsan.l;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;

    private FirebaseAuth mAuth;
    private String currentUserID, code, currentDate, currentTime, message;
    private DatabaseReference UserInfo, messagesChat, messagesCodeRef, GroupMessageKey, reference, rootRef;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private RecyclerView userMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.message_input);


        rootRef = FirebaseDatabase.getInstance().getReference();

        UserInfo = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        messagesChat = FirebaseDatabase.getInstance().getReference().child("Message");
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_chat);


        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToDatabase();
            }
        });

        input_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input_msg, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        UserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("circleMembers")){
                    code = dataSnapshot.child("code").getValue(String.class);
                    displayCode();
                    showMessage();
                }
                else if (!dataSnapshot.child("joincode").getValue(String.class).equals("na")){
                    code = dataSnapshot.child("joincode").getValue(String.class);
                    displayJoinCode();
                    showMessage();
                }
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                    alert.setTitle("Alert !");
                    alert.setMessage("Please join circle or let other join your circle before using chatRoom");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void displayJoinCode() {
        messagesCodeRef = messagesChat.child(code);
    }
    private void displayCode() {
        messagesCodeRef = messagesChat.child(code);
    }


    private void showMessage(){
        rootRef.child("Message").child(code).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                messagesList.add(messages);

                messageAdapter.notifyDataSetChanged();

                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessageToDatabase() {
        message = input_msg.getText().toString();
        String messageKey = messagesCodeRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this,"Please write message", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh;mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            messagesCodeRef.updateChildren(groupMessageKey);

            GroupMessageKey = messagesCodeRef.child(messageKey);

            rootRef.child("users").child(currentUserID).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String currentUserName = dataSnapshot.getValue(String.class);

                    HashMap<String, Object> messageInfoMap = new HashMap<>();
                    messageInfoMap.put("username", currentUserName);
                    messageInfoMap.put("message", message);
                    messageInfoMap.put("date", currentDate);
                    messageInfoMap.put("time", currentTime);
                    messageInfoMap.put("userID", currentUserID);

                    GroupMessageKey.updateChildren(messageInfoMap);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            input_msg.setText("");
        }
    }
}
