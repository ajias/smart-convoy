package com.example.munsan.l;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinCircleActivity extends AppCompatActivity {

    Pinview pinview;
    DatabaseReference reference, currentReference;
    FirebaseUser user;
    FirebaseAuth auth;
    String currentUserId, joinUserId;
    DatabaseReference circleReference, messageJoinRef, joinCodeVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        currentUserId = user.getUid();

        pinview = (Pinview)findViewById(R.id.pinview);

    }

    public void onStart() {
        super.onStart();

        currentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("circleMembers")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(JoinCircleActivity.this);
                    alert.setTitle("Alert !");
                    alert.setMessage("You is an admin for this trip, you cant join other circle");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();
                }
                else{
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void submitBtnClick(View v) {
        // check if the code is available
        Query query = reference.orderByChild("code").equalTo(pinview.getValue());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    CreateUser createUser = null;
                    for (DataSnapshot childDss : dataSnapshot.getChildren()) {

                        createUser = childDss.getValue(CreateUser.class);
                        joinUserId = createUser.userId;

                        circleReference = FirebaseDatabase.getInstance().getReference().child("users").child(joinUserId).child("circleMembers");

                        CircleJoin circleJoin = new CircleJoin(currentUserId);
                        CircleJoin circleJoin1 = new CircleJoin(joinUserId);

                        circleReference.child(user.getUid()).setValue(circleJoin).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "User join circle succesfully", Toast.LENGTH_LONG).show();


                                    joinCodeVal = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("joincode");
                                    joinCodeVal.setValue(pinview.getValue());
                                    //Intent intent = new Intent(JoinCircleActivity.this, MyNavigationActivity.class);
                                    //startActivity(intent);

                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Could not join circle, try Again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });



                    }
                } else {
                    Toast.makeText(getApplicationContext(), "circle code is invalid", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
