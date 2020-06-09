package com.example.emoplayer.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emoplayer.MainActivity;
import com.example.emoplayer.R;
import com.example.emoplayer.SignUp.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextView signUp, forgotPassword;
    EditText emailEt, passwordEt;
    Button loginButton;
    Dialog dialog;
    LinearLayout layout;

    FirebaseAuth mAuth;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        initViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordRecoverDialog();
            }
        });
    }

    private void initViews() {
        emailEt = findViewById(R.id.login_email);
        passwordEt = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.login_forgotPassword);
        loginButton = findViewById(R.id.login_loginButton);
        signUp = findViewById(R.id.login_signUp);
        dialog = new Dialog(this);
        layout = findViewById(R.id.login_layout);
    }

    private void inputValidation() {

        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Invalid Email");
            emailEt.setFocusable(true);
        } else {
            signIn();
        }
    }

    private void signIn() {

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Log.d(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showPasswordRecoverDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //set layout
        LinearLayout linearLayout = new LinearLayout(this);

        //views to be set in the dialog
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        // recover button
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String inputEmail = emailEt.getText().toString().trim();

                if (!inputEmail.equals("")) {
                    beginRecovery(inputEmail);
                }
                else {
                    Snackbar.make(layout, "Please enter email..", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getColor(R.color.colorPrimary))
                            .show();
                }

            }
        });

        // cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginRecovery(String email) {

        showProgressDialog();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                hideProgressDialog();

                if (task.isSuccessful()) {
                    Snackbar.make(layout, "Email Sent", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getColor(R.color.colorPrimary))
                            .show();
                } else {
                    Snackbar.make(layout, "Failed!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getColor(R.color.colorPrimary))
                            .show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                hideProgressDialog();

                Snackbar.make(layout, "Failed!", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(R.color.colorPrimary))
                        .show();
            }
        });

    }

    private void showProgressDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void hideProgressDialog() {
        dialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


