package com.example.admin.scloud.screen.playing;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.admin.s_cloud.R;
import com.example.admin.scloud.data.model.Track;
import com.example.admin.scloud.service.MusicService;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener,
        MusicService.OnMusicChangeListener {


    private TextView mTextTitle, mTextArtist, mTextDuration, mTextCurrentDuration;
    private SeekBar mSeekBar;
    private ImageButton mButtonPrevious, mButtonState, mButtonNext;
    private ImageButton mButtonLoop, mButtonShuffle, mButtonDownload;
    private ImageView mImageBackground;

    private List<Track> mTracks;
    private int mPossition;

    private MusicService mMusicService;
    private Handler mHandler;
    private boolean mBound;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss");

    private ServiceConnection mMediaConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MediaBinder mediaBinder = (MusicService.MediaBinder) iBinder;
            mMusicService = mediaBinder.getMediaService();
           // mMusicService.setMusicChangeListener(PlayingActivity.this);
            Track track = mMusicService.getCurrentTrack();
            setTrack(track);
            updateTimeSong();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mMediaConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mMediaConnection);
        mBound = false;
    }

    private void initView() {
        mTextTitle = findViewById(R.id.text_playing_name);
        mTextArtist = findViewById(R.id.text_playing_artist);
        mTextDuration = findViewById(R.id.text_durationtime);
        mTextCurrentDuration = findViewById(R.id.text_playingtime);
        mSeekBar = findViewById(R.id.seekbar_playing);
        mButtonPrevious = findViewById(R.id.button_previous);
        mButtonState = findViewById(R.id.button_play);
        mButtonNext = findViewById(R.id.button_next);
        mButtonLoop = findViewById(R.id.image_button_NoRepeat);
        //mButtonShuffle = findViewById(R.id.image_button_shuffle);
        mButtonDownload = findViewById(R.id.button_download);
        //mImageBackground = findViewById(R.id.view_picture);
        mButtonState.setOnClickListener(this);
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_play:

                changeState();
                break;
            case R.id.button_next:
                next();
                break;
            case R.id.button_previous:
                previous();
                break;
        }
    }

    private void updateTimeSong(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextCurrentDuration.setText(mDateFormat.format(mMusicService.getCurrentDuration()));
                mSeekBar.setProgress((int) mMusicService.getCurrentDuration());
                handler.postDelayed(this, 500);
            }
        }, 100);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    mMusicService.seekTo(mSeekBar.getProgress());
                } catch (Exception e) {
                    mSeekBar.setProgress(0);
                }
            }
        });
    }

    private void changeState() {
        if (mMusicService == null) return;
        if (mMusicService.isPlaying()) {
            mMusicService.pause();
            mButtonState.setBackgroundResource(R.drawable.ic_play);
        } else {
            mMusicService.resume();
            mButtonState.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        }
    }

    private void next(){
        if (mMusicService == null) return;
        mMusicService.next();
    }

    private void previous(){
        if (mMusicService == null) return;
        mMusicService.previous();
    }

    @Override
    public void onMusicPlay() {
        mButtonState.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    @Override
    public void onMusicPause() {
        mButtonState.setImageResource(R.drawable.ic_play_arrow_24dp);
    }

    @Override
    public void setTrack(Track track) {
        mTextTitle.setText(track.getTitle());
        mTextArtist.setText(track.getArtist().getName());
        mTextDuration.setText(mDateFormat.format(track.getDuration()));
        mSeekBar.setMax(track.getDuration());
    }
}
