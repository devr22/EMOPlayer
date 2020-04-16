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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emoplayer.Model.Model_Songs_Emotion;
import com.example.emoplayer.Model.Model_Songs_Favourites;
import com.example.emoplayer.Music.MusicPlayerActivity;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterEmotionSong extends RecyclerView.Adapter<AdapterEmotionSong.MyHolder> {

    private static final String TAG = "AdapterEmotionSong";
    private static final String SONG_LIST = "songs";
    private static final String SONG_POSITION = "position";


    private Context context;
    private ArrayList<Model_Songs_Emotion> songList;

    FirebaseUser user;
    FirebaseFirestore database;

    public AdapterEmotionSong(Context context, ArrayList<Model_Songs_Emotion> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.emotion_songs_item, parent, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        //get data
        String title = songList.get(position).getSongTitle();
        String artist = songList.get(position).getArtist();
        String image = songList.get(position).getAlbum_art();
        final String userId = user.getUid();

        //set data
        holder.songTitle.setText(title);
        holder.songArtist.setText(artist);
        try {
            Picasso.get().load(image).placeholder(R.drawable.photo_singer_female).into(holder.artistImage);
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: failed to load album image... + " + e.getMessage());
        }

        holder.saveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String songId = songList.get(position).getId();
                String docId = songList.get(position).getSongTitle();

                Model_Songs_Favourites modelSong = new Model_Songs_Favourites(songId, docId);

                database.collection("Users").document(userId)
                        .collection("Favourites").document(docId).set(modelSong)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "saveSong: saved");
                                holder.saveSong.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_pink));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "saveSong: Failed: " + e.getMessage());
                    }
                });

                /*if (holder.saveSong.getDrawable() == ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_pink)){

                    Log.d(TAG, "saveSong: storing");

                    String songId = songList.get(position).getId();
                    String docId = songList.get(position).getSongTitle();

                    Model_Songs_Favourites modelSong = new Model_Songs_Favourites(songId, docId);

                    database.collection("Users").document(userId)
                            .collection("Favourites").document(docId).set(modelSong)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "saveSong: saved");
                                    holder.saveSong.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favorite_pink));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "saveSong: Failed: " + e.getMessage());
                        }
                    });
                }
                else if (holder.saveSong.getDrawable() == ContextCompat.getDrawable(context, R.drawable.ic_favorite_pink)){

                    Log.d(TAG, "saveSong: deleting");

                    String docId = songList.get(position).getSongTitle();

                    database.collection("Users").document(userId)
                            .collection("Favourites").document(docId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "saveSong: Deleted");
                                    holder.saveSong.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_pink));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "saveSong: Failed: " + e.getMessage());
                        }
                    });
                }
*/
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MusicPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SONG_LIST, songList);
                intent.putExtras(bundle);
                intent.putExtra(SONG_POSITION, position);
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

        ImageView artistImage, saveSong;
        TextView songTitle, songArtist;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            artistImage = itemView.findViewById(R.id.emotion_albumImage);
            saveSong = itemView.findViewById(R.id.emotion_saveSong);
            songTitle = itemView.findViewById(R.id.emotion_songTitle);
            songArtist = itemView.findViewById(R.id.emotion_songArtist);

        }
    }

}
