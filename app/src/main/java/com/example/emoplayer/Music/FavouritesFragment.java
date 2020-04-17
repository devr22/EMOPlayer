package com.example.emoplayer.Music;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.emoplayer.Adapter.AdapterFavouritesSong;
import com.example.emoplayer.Model.Model_Songs;
import com.example.emoplayer.Model.Model_Songs_Favourites;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Objects;

public class FavouritesFragment extends Fragment {

    private static final String TAG = "FavouritesFragment";

    public FavouritesFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private FirebaseUser user;
    private FirebaseFirestore database;
    private FirebaseFirestore databaseSongs;

    private ArrayList<Model_Songs_Favourites> songIdList = new ArrayList<>();
    private ArrayList<Model_Songs> songList = new ArrayList<>();
    private AdapterFavouritesSong adapterFavouritesSong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();
        databaseSongs = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.favourites_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFavouriteSongIdList();

        return view;
    }

    private void getFavouriteSongIdList(){

        String userId = user.getUid();

        database.collection("Users").document(userId)
                .collection("Favourites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            Log.d(TAG, "getFavouriteSongIdList: Successful");
                            songIdList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){

                                Model_Songs_Favourites fav_song = document.toObject(Model_Songs_Favourites.class);
                                songIdList.add(fav_song);
                                Log.d(TAG, "getFavouriteSongIdList: data: " + fav_song.getDocID());
                            }
                            Log.d(TAG, "getFavouriteSongIdList: " + songIdList.size());
                            getFavouriteSongList(songIdList);
                        }
                        else {
                            Log.d(TAG, "getFavouriteSongIdList: Error getting documents: " + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "getFavouriteSongIdList: failed: " + e.getMessage());
            }
        });
    }

    private void getFavouriteSongList(ArrayList<Model_Songs_Favourites> songIdList){

        Log.d(TAG, "getFavouriteSongList: " + songIdList.size());

        songList.clear();
        for (Model_Songs_Favourites fav_song : songIdList){

            databaseSongs.collection("Songs").document(fav_song.getSongID()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Log.d(TAG, "getFavouriteSongList: onSuccess");

                            if (documentSnapshot.exists()){
                                Model_Songs song = (Model_Songs) documentSnapshot.getData();
                                songList.add(song);

                                adapterFavouritesSong = new AdapterFavouritesSong(getActivity(), songList);
                                recyclerView.setAdapter(adapterFavouritesSong);
                            }
                            else {
                                Log.d(TAG, "getFavouriteSongList: document does not exist");
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "getFavouriteSongList: failed: " + e.getMessage());
                }
            });
        }
    }

}



