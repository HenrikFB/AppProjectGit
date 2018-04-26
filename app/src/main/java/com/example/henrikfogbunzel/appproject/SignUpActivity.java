package com.example.henrikfogbunzel.appproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    ProgressBar progressBar;

    Button signupBtnSignUp;
    Button signupBtnLogin;

    EditText signupEditTextEmail;
    EditText signupEditTextPassword;

    private FirebaseAuth mAuth;

    static final int REQ_PROFILE= 112;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupBtnLogin = (Button) findViewById(R.id.signupBtnLogin);
        signupBtnSignUp = (Button) findViewById(R.id.signupBtnSignUp);

        signupEditTextEmail =(EditText) findViewById(R.id.signupEditTextEmail);
        signupEditTextPassword = (EditText) findViewById(R.id.signupEditTextPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        signupBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


        signupBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    private void registerUser() {
        String email = signupEditTextEmail.getText().toString().trim();
        String password = signupEditTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            signupEditTextEmail.setError("Email is required");
            signupEditTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signupEditTextEmail.setError("Please enter a valid email");
            signupEditTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            signupEditTextPassword.setError("Password is required");
            signupEditTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            signupEditTextPassword.setError("Minimum lenght of password should be 6");
            signupEditTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    //finish();//nye tilføjelser
                    Toast.makeText(getApplicationContext(), "User Registrered succesfull", Toast.LENGTH_SHORT).show();
                    startProfileActivity();
                } else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "User is already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void startProfileActivity() {
        finish();
        Intent startProfileActivityIntent = new Intent(SignUpActivity.this, ProfileActivity.class);
        startProfileActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(startProfileActivityIntent, REQ_PROFILE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
/*
    private void loadUserInformation(){
        FirebaseUser user = mAuth.getCurrentUser();

    }
*/
}

