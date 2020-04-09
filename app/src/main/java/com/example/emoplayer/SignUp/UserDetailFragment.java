package com.example.emoplayer.SignUp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.emoplayer.Login.LoginActivity;
import com.example.emoplayer.Model.Model_Users;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class UserDetailFragment extends Fragment {

    private static final String TAG = "UserDetailFragment";

    public UserDetailFragment() {
        // Required empty public constructor
    }

    private Spinner spinnerGender, spinnerCountry;
    private EditText userNameEt, nameEt;

    private FirebaseFirestore database;
    private FirebaseUser user;

    private String gender;
    private String countryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();

        spinnerGender = view.findViewById(R.id.user_detail_spinnerGender);
        spinnerCountry = view.findViewById(R.id.user_detail_spinnerCountry);
        userNameEt = view.findViewById(R.id.user_detail_userName);
        nameEt = view.findViewById(R.id.user_detail_name);
        TextView skip = view.findViewById(R.id.user_detail_skip);
        ImageButton submit = view.findViewById(R.id.user_detail_submit);

        getGenderList();
        getCountryList();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;

    }

    private void getGenderList() {

        final String[] genders = {"Male", "Female", "Transgender"};
        ArrayAdapter<String> arrayAdapterGender;
        arrayAdapterGender = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, genders);
        spinnerGender.setAdapter(arrayAdapterGender);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = genders[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getCountryList() {

        Locale[] locales = Locale.getAvailableLocales();
        final ArrayList<String> countries = new ArrayList<>();
        String country;

        for (Locale loc : locales) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, countries);
        spinnerCountry.setAdapter(arrayAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countryName = countries.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateUserInfo() {

        String uid = user.getUid();
        String email = user.getEmail();
        String userName = userNameEt.getText().toString().trim();
        String name = nameEt.getText().toString().trim();

        Model_Users model_user = new Model_Users(uid, email, userName, name, gender, countryName);

        database.collection("Users").document(uid).set(model_user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateUserInfo: Saved");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "updateUserInfo: Error");
                    }
                });

    }


}
