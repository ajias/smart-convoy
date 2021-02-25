package com.example.munsan.l;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LeaveCircleNew extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UserInfo, AdminInfo, resetJoincode, AdminInfor, resetUsingCode;
    private String currentUserID, code, hasil, AdminUID;

    TextView currentCode;
    Button yesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_circle_new);

        mAuth = FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();
        UserInfo = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        AdminInfo = FirebaseDatabase.getInstance().getReference().child("users");

        currentCode = (TextView)findViewById(R.id.currentCircle);
        yesButton = (Button)findViewById(R.id.yesButton);

        UserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("circleMembers")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(LeaveCircleNew.this);
                    alert.setTitle("Alert !");
                    alert.setMessage("You is an admin for this trip, you cant leave you own circle");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();
                }
                else if (!dataSnapshot.child("joincode").getValue(String.class).equals("na")){
                    code = dataSnapshot.child("joincode").getValue(String.class);
                    setCode();
                }
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(LeaveCircleNew.this);
                    alert.setTitle("Alert !");
                    alert.setMessage("You does not join any circle. Please join any circle in order to use this features");
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


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetJoincode = UserInfo.child("joincode");
                resetJoincode.setValue("na");
                resetUsingCode = UserInfo.child("usingCode");
                resetUsingCode.setValue("na");
                getMemberUID();
                finish();
            }
        });

        }

    private void getMemberUID() {
        AdminInfo.orderByChild("code").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    AdminInfor = FirebaseDatabase.getInstance().getReference().child("users").child(key).child("circleMembers").child(currentUserID).child("circlememberid");
                    AdminInfor.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCode(){
        currentCode.setText(code);
    }
}
