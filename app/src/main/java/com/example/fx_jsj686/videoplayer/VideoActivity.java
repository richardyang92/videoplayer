package com.example.fx_jsj686.videoplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * Created by fx-jsj686 on 17-4-17.
 */

public class VideoActivity extends AppCompatActivity
        implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "VideoActivity";
    private String mVideoPath = null;
    private SurfaceView mSurfaceView = null;
    private MediaPlayer mPlayer = null;
    private SeekBar mSeekBar = null;
    private Thread mSeekBarUpdateThread = null;

    private static Integer sProgress = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);
        Bundle bundle = this.getIntent().getExtras();
        String videoName = bundle.getString(Consts.VIDEO_NAME);
        Log.i(TAG, "onCreate: video name is " + videoName);
        mVideoPath = videoName;

        mSurfaceView = (SurfaceView) findViewById(R.id.videoplayer);
        mSurfaceView.getHolder().addCallback(this);

        mSeekBar = (SeekBar) findViewById(R.id.videoseekbar);
        mSeekBar.setOnSeekBarChangeListener(VideoActivity.this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared: player prepared");
        if (mPlayer != null) {
            Log.i(TAG, "onPrepared: playing...");
            mPlayer.start();

            mSeekBarUpdateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (VideoActivity.this) {
                            sProgress = mPlayer.getCurrentPosition();
                            Log.w(TAG, "run: progress is " + sProgress);
                            mSeekBar.setProgress(sProgress);
                        }
                    }
                }
            });
            mSeekBarUpdateThread.start();
        }
    }

    private void init() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(mSurfaceView.getHolder());
        boolean isOk = false;
        try {
            mPlayer.setDataSource(mVideoPath);
            mPlayer.setOnPreparedListener(VideoActivity.this);
            mPlayer.prepare();
            mSeekBar.setMax(mPlayer.getDuration());
            isOk = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isOk) {
            deinit();
        }
        Log.i(TAG, "init player " + (isOk ? "OK" : "Failed"));
    }

    private void deinit() {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        Log.i(TAG, "deinit player done");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        deinit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            synchronized (VideoActivity.this) {
                sProgress = progress;
                Log.w(TAG, "onProgressChanged: progress is" + sProgress);
                mPlayer.seekTo(sProgress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
