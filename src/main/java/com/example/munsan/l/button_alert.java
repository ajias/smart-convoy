package com.example.munsan.l;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class button_alert extends AppCompatActivity {

    private Button toilet,gas,emergency;


    FirebaseAuth mAuth;
    DatabaseReference event,userInfo, usingCode, notification;
    String uid, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_alert);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        event = FirebaseDatabase.getInstance().getReference().child("Event");
        userInfo = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        toilet = (Button)findViewById(R.id.toilet);
        gas = (Button)findViewById(R.id.gas);
        emergency = (Button)findViewById(R.id.emergency);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.equals(toilet)){

                userInfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("circleMembers")){
                            code = dataSnapshot.child("code").getValue(String.class);
                            usingCode = userInfo.child("usingCode");
                            usingCode.setValue(code);

                            notification = FirebaseDatabase.getInstance().getReference().child("Event").child(code).child("notification");
                            notification.setValue("1");
                        }
                        else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                            code = dataSnapshot.child("joincode").getValue(String.class);
                            usingCode = userInfo.child("usingCode");
                            usingCode.setValue(code);

                            event.child(code).child("notification").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    notification.setValue("1");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }else{
                            Toast.makeText(getApplicationContext(), "Please join circle or let other join your circle before using this alert button !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }
                else if(v.equals(gas)){
                    userInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("circleMembers")){
                                code = dataSnapshot.child("code").getValue(String.class);
                                usingCode = userInfo.child("usingCode");
                                usingCode.setValue(code);

                                event.child(code).child("notification").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        notification.setValue("2");

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                                code = dataSnapshot.child("joincode").getValue(String.class);
                                usingCode = userInfo.child("usingCode");
                                usingCode.setValue(code);

                                event.child(code).child("notification").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        notification.setValue("3");

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                Toast.makeText(getApplicationContext(), "Please join circle or let other join your circle before using this alert button !", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    userInfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("circleMembers")){
                            code = dataSnapshot.child("code").getValue(String.class);
                            usingCode = userInfo.child("usingCode");
                            usingCode.setValue(code);

                            event.child(code).child("notification").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    notification.setValue("3");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                            code = dataSnapshot.child("joincode").getValue(String.class);
                            usingCode = userInfo.child("usingCode");
                            usingCode.setValue(code);

                            event.child(code).child("notification").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    notification.setValue("3");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "Please join circle or let other join your circle before using this alert button !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
    };
        toilet.setOnClickListener(listener);
        gas.setOnClickListener(listener);
        emergency.setOnClickListener(listener);

        getNotification();


}

    private void getNotification() {
        userInfo.child("usingCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue(String.class).equals("na")){
                    String iniCode = dataSnapshot.getValue(String.class);
                    event = FirebaseDatabase.getInstance().getReference().child("Event");

                    event.child(iniCode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("notification")){

                                if(dataSnapshot.child("notification").getValue().equals("1")){
                                    Toast.makeText(getApplicationContext(), "Hello , someone wanna go to toilet , please respond in chatRoom", Toast.LENGTH_LONG).show();

                                }else if(dataSnapshot.child("notification").getValue().equals("2")){
                                    Toast.makeText(getApplicationContext(), "Hello , someone wanna go to Gas Station behind , please respond in chatRoom", Toast.LENGTH_LONG).show();


                                }else if(dataSnapshot.child("notification").getValue().equals("3")){
                                    Toast.makeText(getApplicationContext(), "Hello , someone in emergency , please respond in chatRoom", Toast.LENGTH_LONG).show();

                                    }else{
                                //do nothing
                            }
                        }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
