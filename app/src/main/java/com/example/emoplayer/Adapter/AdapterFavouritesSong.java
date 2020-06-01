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

import com.example.emoplayer.Model.Model_Songs;
import com.example.emoplayer.Music.MusicPlayerActivity;
import com.example.emoplayer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFavouritesSong extends RecyclerView.Adapter<AdapterFavouritesSong.MyHolder> {

    private static final String TAG = "AdapterFavouritesSong";

    private FirebaseUser user;
    private FirebaseFirestore database;

    private Context context;
    private ArrayList<Model_Songs> songList;

    public AdapterFavouritesSong(Context context, ArrayList<Model_Songs> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.favourites_songs_item, parent, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        // get data
        String title = songList.get(position).getSongTitle();
        String artist = songList.get(position).getArtist();
        String image = songList.get(position).getAlbum_art();

        // set data
        holder.songTitle.setText(title);
        holder.songArtist.setText(artist);
        try {
            Picasso.get().load(image).placeholder(R.drawable.photo_singer_female).into(holder.albumImage);
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: failed to load album image... + " + e.getMessage());
        }

        holder.saveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteSongFromFavouriteList(holder, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(String.valueOf(R.string.SONG_LIST), songList);
                intent.putExtras(bundle);
                intent.putExtra(String.valueOf(R.string.SONG_POSITION), songList);
                intent.putExtra(String.valueOf(R.string.SOURCE), String.valueOf(R.string.FAV_SONGS));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        ImageView albumImage, saveSong;
        TextView songTitle, songArtist;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.fav_albumImage);
            saveSong = itemView.findViewById(R.id.fav_saved);
            songTitle = itemView.findViewById(R.id.fav_songTitle);
            songArtist = itemView.findViewById(R.id.fav_songArtist);

        }
    }

    private void deleteSongFromFavouriteList(final MyHolder holder, int position){

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
