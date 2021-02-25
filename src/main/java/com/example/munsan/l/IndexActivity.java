package com.example.munsan.l;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IndexActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null) {
            setContentView(R.layout.activity_index);
        } else {
            Intent intent = new Intent(IndexActivity.this, MyNavigationActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public void goToSingIn(View v){
        Intent intent = new Intent(IndexActivity.this, signInActivity.class);
        startActivity(intent);
    }
    public void goToSingUp(View v){
        Intent intent = new Intent(IndexActivity.this, signUpActivity.class);
        startActivity(intent);
    }
}
