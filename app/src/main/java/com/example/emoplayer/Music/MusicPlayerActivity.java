package com.example.emoplayer.Music;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emoplayer.Model.Model_Songs_Emotion;
import com.example.emoplayer.R;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayerActivity";
    public static final String SONG_LIST = "songs";
    private static final String SONG_POSITION = "position";

    private CircularImageView song_image;
    private TextView tv_song_title, tv_song_artist;
    private JcPlayerView jcPlayerView;

    int songPosition;
    ArrayList<Model_Songs_Emotion> songList;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initViews();
        initJcAudioPlayList();

        jcPlayerView.playAudio(jcAudios.get(songPosition));

    }

    private void initViews(){

        song_image = findViewById(R.id.musicPlayer_song_image);
        tv_song_artist = findViewById(R.id.musicPlayer_tv_song_artist);
        tv_song_title = findViewById(R.id.musicPlayer_tv_song_title);
        jcPlayerView = findViewById(R.id.musicPlayer_jcPlayer);
    }

    private void initJcAudioPlayList(){

        Bundle bundle = getIntent().getExtras();
        songList = (ArrayList<Model_Songs_Emotion>) bundle.getSerializable(SONG_LIST);
        songPosition = getIntent().getIntExtra(SONG_POSITION, 0);


        for (Model_Songs_Emotion song : songList){
            jcAudios.add(JcAudio.createFromURL(song.getSongTitle(), song.getSongLink()));
        }
        jcPlayerView.initPlaylist(jcAudios, null);

    }

}
