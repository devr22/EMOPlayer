package com.example.emoplayer.Music;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emoplayer.Model.Model_Songs;
import com.example.emoplayer.R;
import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements JcPlayerManagerListener {

    private static final String TAG = "MusicPlayerActivity";

    private CircularImageView song_image;
    private TextView tv_song_title, tv_song_artist;
    private JcPlayerView jcPlayerView;

    int songPosition;
    String songSource;
    ArrayList<Model_Songs> songList_emotion;
    ArrayList<Model_Songs> songList_fav;
    ArrayList<Model_Songs> songList_recommended;

    ArrayList<JcAudio> jcAudios = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initViews();
        initJcAudioPlayList();

        jcPlayerView.playAudio(jcAudios.get(songPosition));
        jcPlayerView.createNotification();


    }

    private void initViews(){

        song_image = findViewById(R.id.musicPlayer_song_image);
        tv_song_artist = findViewById(R.id.musicPlayer_tv_song_artist);
        tv_song_title = findViewById(R.id.musicPlayer_tv_song_title);
        jcPlayerView = findViewById(R.id.musicPlayer_jcPlayer);
    }

    private void initJcAudioPlayList(){

        Bundle bundle = getIntent().getExtras();
        songPosition = getIntent().getIntExtra(String.valueOf(R.string.SONG_POSITION), 0);
        songSource = getIntent().getStringExtra(String.valueOf(R.string.SOURCE));

        assert songSource != null;
        if (songSource.equals(String.valueOf(R.string.EMOTION_SONGS))){

            songList_emotion = (ArrayList<Model_Songs>) bundle.getSerializable(String.valueOf(R.string.SONG_LIST));

            for (Model_Songs song : songList_emotion){
                jcAudios.add(JcAudio.createFromURL(song.getSongTitle(), song.getSongLink()));
            }
        }
        else if (songSource.equals(String.valueOf(R.string.RECOMMENDED_SONGS))){

            songList_recommended = (ArrayList<Model_Songs>) bundle.getSerializable(String.valueOf(R.string.SONG_LIST));

            for (Model_Songs song : songList_recommended){
                jcAudios.add(JcAudio.createFromURL(song.getSongTitle(), song.getSongLink()));
            }
        }
        else {
            songList_fav = (ArrayList<Model_Songs>) bundle.getSerializable(String.valueOf(R.string.SONG_LIST));

            for (Model_Songs song : songList_fav){
                jcAudios.add(JcAudio.createFromURL(song.getSongTitle(), song.getSongLink()));
            }
        }

        jcPlayerView.initPlaylist(jcAudios, null);

    }

    private void rotateImageAlbum() {

        /*if (!shouldRotateImage)
            return;*/
        song_image.animate().setDuration(100).rotation(song_image.getRotation() + 2f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateImageAlbum();
                super.onAnimationEnd(animation);
            }
        });
    }

    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onContinueAudio(JcStatus jcStatus) {

    }

    @Override
    public void onJcpError(Throwable throwable) {

    }

    @Override
    public void onPaused(JcStatus jcStatus) {

    }

    @Override
    public void onPlaying(JcStatus jcStatus) {

    }

    @Override
    public void onPreparedAudio(JcStatus jcStatus) {
        
    }

    @Override
    public void onStopped(JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged(JcStatus jcStatus) {

    }
}
