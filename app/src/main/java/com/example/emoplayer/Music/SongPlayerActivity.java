package com.example.emoplayer.Music;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.emoplayer.R;
import com.example.emoplayer.Utils.MusicUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
    private MusicUtils utils;

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

        // set Progress bar values
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        // Media Player
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Changing button image to play button
                bt_play.setImageResource(R.drawable.ic_play_arrow);
            }
        });

        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AssetFileDescriptor afd = getAssets().openFd("short_music.mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }

        utils = new MusicUtils();
        // Listeners
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                mHandler.post(mUpdateTimeTask);
            }
        });

        buttonPlayerAction();
        updateTimerAndSeekbar();

    }

    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */
    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    bt_play.setImageResource(R.drawable.ic_pause);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }
                rotateImageAlbum();
            }
        });
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();

            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Displaying Total Duration time
        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    private void rotateImageAlbum() {
        if (!mp.isPlaying()) return;
        song_image.animate().setDuration(100).rotation(song_image.getRotation() + 2f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateImageAlbum();
                super.onAnimationEnd(animation);
            }
        });
    }

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }
}









