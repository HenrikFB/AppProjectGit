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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button mainBtnLogin;
    Button mainBtnSignUp;

    EditText mainEditTextEmail;
    EditText mainEditTextPassword;

    ProgressBar progressBar;

    static final int REQ_SIGNUP= 112;
    static final int REQ_PROFILE= 112;

    private FirebaseAuth mAuth;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainBtnLogin = (Button) findViewById(R.id.mainBtnLogin);
        mainBtnSignUp = (Button) findViewById(R.id.mainBtnSignUp);


        mainEditTextEmail = (EditText) findViewById(R.id.mainEditTextEmail);
        mainEditTextPassword = (EditText) findViewById(R.id.mainEditTextPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);





        mainBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();//nye tilføjelser.
                startSignUpActivity();
            }
        });

        mainBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

    }

    private void userLogin() {
        String email = mainEditTextEmail.getText().toString().trim();
        String password = mainEditTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            mainEditTextEmail.setError("Email is required");
            mainEditTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mainEditTextEmail.setError("Please enter a valid email");
            mainEditTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            mainEditTextPassword.setError("Password is required");
            mainEditTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            mainEditTextPassword.setError("Minimum lenght of password should be 6");
            mainEditTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            //finish();//nye med at være logget ind
                            startProfileActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void startProfileActivity() {
        finish();
        Intent startProfileActivityIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startProfileActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(startProfileActivityIntent, REQ_PROFILE);
    }

    private void startSignUpActivity() {
        finish();
        Intent startSignUpAcitivityIntent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivityForResult(startSignUpAcitivityIntent, REQ_SIGNUP);
    }
/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("text", mainEditTextEmail.getText().toString());
    }
*/

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }

}
