package com.example.emoplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emoplayer.Model.Model_Songs;
import com.example.emoplayer.Model.Model_Songs_Favourites;
import com.example.emoplayer.Music.MusicPlayerActivity;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterEmotionSong extends RecyclerView.Adapter<AdapterEmotionSong.MyHolder> {

    private static final String TAG = "AdapterEmotionSong";

    private FirebaseUser user;
    private FirebaseFirestore database;
    private FirebaseFirestore databaseFavSongs;

    private ArrayList<Model_Songs_Favourites> favSongsList = new ArrayList<>();

    private Context context;
    private ArrayList<Model_Songs> songList;

    public AdapterEmotionSong(Context context, ArrayList<Model_Songs> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.emotion_songs_item, parent, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();
        databaseFavSongs = FirebaseFirestore.getInstance();

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        //get data
        String userId = user.getUid();
        String title = songList.get(position).getSongTitle();
        String artist = songList.get(position).getArtist();
        String image = songList.get(position).getAlbum_art();
        final String id = songList.get(position).getId();

        //set data
        holder.songTitle.setText(title);
        holder.songArtist.setText(artist);
        try {
            Picasso.get().load(image).placeholder(R.drawable.photo_singer_female).into(holder.albumImage);
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: failed to load album image... + " + e.getMessage());
        }

        databaseFavSongs.collection("Users").document(userId)
                .collection("Favourites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            favSongsList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Model_Songs_Favourites favSong = document.toObject(Model_Songs_Favourites.class);
                                favSongsList.add(favSong);
                            }
                            Log.d(TAG, "onBindViewHolder: " + favSongsList.size());

                            for (Model_Songs_Favourites song : favSongsList){
                                if (song.getSongID() != null && song.getSongID().equals(id)){
                                    holder.saveSong.setImageDrawable(ContextCompat.getDrawable(context,
                                            R.drawable.ic_favorite_pink));
                                }
                                else{
                                    Log.d(TAG, "onBindViewHolder: else");
                                }
                            }

                        }
                        else {
                            Log.d(TAG, "onBindViewHolder: Error in getting documents: " + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onBindViewHolder: failed: " + e.getMessage());
            }
        });



        holder.saveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.saveSong.getDrawable().getConstantState() == ContextCompat.getDrawable(context,
                        R.drawable.ic_favorite_border_pink).getConstantState()) {

                    Log.d(TAG, "saveSong: if: " + holder.saveSong.getDrawable().toString());
                    saveSongInFavouritesList(holder, position);
                } else {
                    Log.d(TAG, "saveSong: else: " + holder.saveSong.getDrawable().toString());
                    deleteSongFromFavouriteList(holder, position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MusicPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(String.valueOf(R.string.SONG_LIST), songList);
                intent.putExtras(bundle);
                intent.putExtra(String.valueOf(R.string.SONG_POSITION), position);
                intent.putExtra(String.valueOf(R.string.SOURCE), String.valueOf(R.string.EMOTION_SONGS));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // View Holder Class
    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView albumImage, saveSong;
        TextView songTitle, songArtist;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.emotion_albumImage);
            saveSong = itemView.findViewById(R.id.emotion_saveSong);
            songTitle = itemView.findViewById(R.id.emotion_songTitle);
            songArtist = itemView.findViewById(R.id.emotion_songArtist);

        }
    }

   /* private ArrayList<Model_Songs_Favourites> getFavSongList() {

        final ArrayList<Model_Songs_Favourites> favSongs = new ArrayList<>();
        String userID = user.getUid();

        databaseFavSongs.collection("Users").document(userID)
                .collection("Favourites").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        favSongs.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            Model_Songs_Favourites song = document.toObject(Model_Songs_Favourites.class);
                            favSongs.add(song);
                        }
                        Log.d(TAG, "getFavSongList: " + favSongs.size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getFavSongList: failed: " + e.getMessage());
            }
        });

        return favSongs;
    }*/

    private void saveSongInFavouritesList(final MyHolder holder, int position) {

        String userId = user.getUid();
        String songId = songList.get(position).getId();
        String docId = songList.get(position).getSongTitle();

        Model_Songs_Favourites songs_favourites = new Model_Songs_Favourites(docId, songId);

        database.collection("Users").document(userId)
                .collection("Favourites").document(docId)
                .set(songs_favourites)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "saveSongInFavouritesList: successful");
                        holder.saveSong.setImageDrawable(ContextCompat.getDrawable(context,
                                R.drawable.ic_favorite_pink));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "saveSongInFavouritesList: failed" + e.getMessage());
            }
        });
    }

    private void deleteSongFromFavouriteList(final MyHolder holder, int position) {

        String userId = user.getUid();
        String docId = songList.get(position).getSongTitle();

        database.collection("Users").document(userId)
                .collection("Favourites").document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "deleteSongFromFavouriteList: deleted");
                        holder.saveSong.setImageDrawable(ContextCompat.getDrawable(context,
                                R.drawable.ic_favorite_border_pink));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "deleteSongFromFavouriteList: failed: " + e.getMessage());
            }
        });
    }

}




