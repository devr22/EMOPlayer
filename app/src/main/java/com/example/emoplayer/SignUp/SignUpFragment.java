package com.example.emoplayer.SignUp;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.emoplayer.Model.Model_Users;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    public SignUpFragment() {
        // Required empty public constructor
    }

    private ImageButton submit, passwordToggle, confirmPasswordToggle;
    private EditText emailEt, passwordEt, confirmPasswordEt;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        initView(view);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHidePassword(passwordEt, passwordToggle);
            }
        });

        confirmPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHidePassword(confirmPasswordEt, confirmPasswordToggle);
            }
        });

        return view;
    }

    private void initView(View view) {

        submit = view.findViewById(R.id.signIp_submit);
        emailEt = view.findViewById(R.id.signUp_email);
        passwordEt = view.findViewById(R.id.signUp_password);
        confirmPasswordEt = view.findViewById(R.id.signUp_confirmPassword);
        passwordToggle = view.findViewById(R.id.signUp_passwordToggle);
        confirmPasswordToggle = view.findViewById(R.id.signUp_confirmPasswordToggle);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Registering User...");
    }

    private void showOrHidePassword(EditText editText, ImageButton imageButton) {

        if (imageButton.getDrawable().getConstantState() == ContextCompat.getDrawable(getActivity(), R.drawable.ic_eye).getConstantState()) {
            imageButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_eye_grey));
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        else {
            imageButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_eye));
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.setSelection(editText.length());
    }

    private void createAccount() {

        String password = passwordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();

        //password matching
        if (password.equals(confirmPassword)) {
            beginRegistration();
        } else {
            confirmPasswordEt.setError("Entered text does not matches password field");
            confirmPasswordEt.setFocusable(true);
        }

    }

    private void beginRegistration() {

        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Invalid Email");
            emailEt.setFocusable(true);
        } else if (password.length() < 6) {
            passwordEt.setError("Password length can't be less than 6 characters");
            passwordEt.setFocusable(true);
        } else {
            registerUser(email, password);
        }

    }

    private void registerUser(String email, String password) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG, "registerUser:success");
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d(TAG, "registerUser: " + user.getUid());
                                storeUserInfo(user);
                            }

                            callUserDetailFragment();

                        } else {
                            Log.d(TAG, "registerUser:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callUserDetailFragment() {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.registration_container, userDetailFragment, "");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void storeUserInfo(FirebaseUser user) {

        String email = user.getEmail();
        String uid = user.getUid();

        Model_Users model_user = new Model_Users(uid, email, "", "", "", "");

        database.collection("Users").document(uid).set(model_user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "storeUserInfo: Saved");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "storeUserInfo: Error");
                    }
                });

    }

}
