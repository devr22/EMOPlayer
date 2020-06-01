package com.example.emoplayer.Music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emoplayer.Login.LoginActivity;
import com.example.emoplayer.Model.Model_Songs_Favourites;
import com.example.emoplayer.Model.Model_Users;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView displayNameTV;
    private TextView nameTV;
    private TextView genderTV;
    private TextView emailTV;
    private TextView countryTV;
    private TextView favouritesTV;
    private Button logoutButton;

    private FirebaseUser user;
    private FirebaseFirestore databaseUser;
    private FirebaseFirestore databaseSongs;

    private ArrayList<Model_Songs_Favourites> songList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseFirestore.getInstance();
        databaseSongs = FirebaseFirestore.getInstance();

        retrieveUserDetail();
        getCountOfFavouritesSong();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

        return view;
    }

    private void initViews(View view) {

        displayNameTV = view.findViewById(R.id.profile_displayNameTV);
        nameTV = view.findViewById(R.id.profile_nameTV);
        genderTV = view.findViewById(R.id.profile_genderTV);
        emailTV = view.findViewById(R.id.profile_emailTV);
        countryTV = view.findViewById(R.id.profile_countryTV);
        favouritesTV = view.findViewById(R.id.profile_favouriteTV);
        logoutButton = view.findViewById(R.id.profile_logoutButton);
    }


    private void retrieveUserDetail() {

        String userId = user.getUid();

        databaseUser.collection("Users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Model_Users modelUser = document.toObject(Model_Users.class);
                                showUserDetail(modelUser);
                            }
                        } else {
                            Log.d(TAG, "retrieveAndShowUserDetail: Error in getting document: " + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "retrieveAndShowUserDetail: failed: " + e.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showUserDetail(Model_Users user) {

        setDisplayName(user);

        if (user.getName().equals("")) {
            nameTV.setText("Unknown");
        } else {
            nameTV.setText(user.getName());
        }

        if (user.getGender().equals("")) {
            genderTV.setText("Unknown");
        } else {
            genderTV.setText(user.getGender());
        }

        emailTV.setText(user.getEmail());

        if (user.getCountry().equals("")) {
            countryTV.setText("Unknown");
        } else {
            countryTV.setText(user.getCountry());
        }
    }

    private void setDisplayName(Model_Users user) {

        char c;
        String display;

        if (user.getUserName().equals("")) {
            c = user.getEmail().charAt(0);
        }

        c = user.getUserName().charAt(0);
        display = Character.toString(c);

        displayNameTV.setText(display);

    }

    private void getCountOfFavouritesSong(){

        String userId = user.getUid();

        databaseSongs.collection("Users").document(userId)
                .collection("Favourites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            songList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){

                                Model_Songs_Favourites song = document.toObject(Model_Songs_Favourites.class);
                                songList.add(song);
                            }
                            favouritesTV.setText(String.valueOf(songList.size()));
                        }
                        else {
                            Log.d(TAG, "getCountOfFavouritesSong: Error in getting document: " + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "getCountOfFavouritesSong: failed: " + e.getMessage());
            }
        });
    }

    private void LogOut(){

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}



