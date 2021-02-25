package com.example.munsan.l;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class signUpActivity extends AppCompatActivity {

    EditText e3_email;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        e3_email = (EditText) findViewById(R.id.editText3);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
    }

    public void goToPasswordAcitivy(View v){
        dialog.setMessage("checking email address");
        dialog.show();

        auth.fetchProvidersForEmail(e3_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            boolean check = !task.getResult().getProviders().isEmpty();
                            if(!check){
                                Intent myIntent = new Intent(signUpActivity.this, PasswordActivity.class);
                                myIntent.putExtra("email", e3_email.getText().toString());
                                startActivity(myIntent);
                                finish();

                            } else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "this email already registered", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
