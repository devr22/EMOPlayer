package com.example.emoplayer.Music;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.emoplayer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;

public class SongPlayerActivity extends AppCompatActivity {

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton bt_play;
    private TextView tv_song_current_duration, tv_song_total_duration, tv_song_title, tv_song_artist;
    private CircularImageView song_image;
    private ImageButton bt_prev, bt_next, bt_repeat, bt_shuffle;

    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    //private SongsManager songManager;
//    private MusicUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        initViews();

    }

    private void initViews() {

        parent_view = findViewById(R.id.player_parentView);
        seek_song_progressbar = findViewById(R.id.player_seek_song_progressbar);
        bt_play = findViewById(R.id.player_bt_play);
        bt_prev = findViewById(R.id.player_bt_prev);
        bt_next = findViewById(R.id.player_bt_next);
        bt_repeat = findViewById(R.id.player_bt_repeat);
        bt_shuffle = findViewById(R.id.player_bt_shuffle);
        tv_song_current_duration = findViewById(R.id.player_tv_song_current_duration);
        tv_song_total_duration = findViewById(R.id.player_tv_song_total_duration);
        tv_song_title = findViewById(R.id.player_tv_song_title);
        tv_song_artist = findViewById(R.id.player_tv_song_artist);
        song_image = findViewById(R.id.player_song_image);
    }

}









