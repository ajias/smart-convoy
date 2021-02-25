package com.example.munsan.l;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class inviteCodeActivity extends AppCompatActivity {

    String name, email, password, date, isSharing, code;
    Uri imageUri;
    TextView tv1;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference references, messagesRef, eventRef;
    //StorageReference storageReference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        tv1 = (TextView) findViewById(R.id.textView1);
        dialog = new ProgressDialog(this);


        auth = FirebaseAuth.getInstance();
        references = FirebaseDatabase.getInstance().getReference().child("users");
        //storageReference = FirebaseStorage.getInstance().getReference().child("user_images");

        messagesRef = FirebaseDatabase.getInstance().getReference().child("Message");
        eventRef = FirebaseDatabase.getInstance().getReference().child("Event");


        Intent myIntent = getIntent();
        if (myIntent != null) {
            name = myIntent.getStringExtra("name");
            email = myIntent.getStringExtra("email");
            password = myIntent.getStringExtra("password");
            code = myIntent.getStringExtra("code");
            isSharing = myIntent.getStringExtra("isSharing");
            imageUri = myIntent.getParcelableExtra("imageUri");

        }
        tv1.setText(code);

    }



    public void registerUser(View v){
        dialog.setMessage("please wait while we are creating account for you");
        dialog.show();


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            user = auth.getCurrentUser();
                            CreateUser createUser = new CreateUser(name, email, password, code, "false", "na", "na", "na", user.getUid(),"na", "na");

                            user = auth.getCurrentUser();
                            userId = user.getUid();

                            references.child(userId).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        /*StorageReference sr = storageReference.child(user.getUid() + ".jpg");
                                        sr.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    String download_image_path = task.getResult().getDownloadUrl().toString();
                                                    references.child(user.getUid()).child("imageUrl").setValue(download_image_path).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){*/

                                                                dialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "user created successfully", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(inviteCodeActivity.this, MyNavigationActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                                /*} else {

                                                                dialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "An error while creating account", Toast.LENGTH_LONG).show();
                                                                }
                                                        }
                                                    });
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            });*/
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            Map<String, Object> map = new HashMap<String,Object>();
                            map.put(code,"");
                            messagesRef.updateChildren(map);

                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put(code,"");
                            eventRef.updateChildren(map2);
                        }
                    }
                });
    }

}
