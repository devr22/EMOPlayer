package com.example.emoplayer.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emoplayer.Model.Model_Songs_Emotion;
import com.example.emoplayer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterEmotionSong extends RecyclerView.Adapter<AdapterEmotionSong.MyHolder> {

    private static final String TAG = "AdapterEmotionSong";

    private Context context;
    private ArrayList<Model_Songs_Emotion> songList;

    public AdapterEmotionSong(Context context, ArrayList<Model_Songs_Emotion> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.emotion_songs_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //get data
        String title = songList.get(position).getSongTitle();
        String artist = songList.get(position).getArtist();
        String image = songList.get(position).getAlbum_art();

        //set data
        holder.songTitle.setText(title);
        holder.songArtist.setText(artist);
        try {
            Picasso.get().load(image).placeholder(R.drawable.photo_singer_female).into(holder.artistImage);
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: failed to load album image... + " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // View Holder Class
    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView artistImage, savedImageView;
        TextView songTitle, songArtist;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            artistImage = itemView.findViewById(R.id.emotion_albumImage);
            savedImageView = itemView.findViewById(R.id.emotion_saved);
            songTitle = itemView.findViewById(R.id.emotion_songTitle);
            songArtist = itemView.findViewById(R.id.emotion_songArtist);

        }
    }

}
