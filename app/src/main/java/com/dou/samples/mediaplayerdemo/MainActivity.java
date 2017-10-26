package com.dou.samples.mediaplayerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    CustomMediaplayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaPlayer = findViewById(R.id.mediaplayer);

        mMediaPlayer.setController(new CustomMediaController(this));
        mMediaPlayer.setUp("android.resource://"+getPackageName()+"/" + R.raw.test_mp4);
        mMediaPlayer.start();
    }
}
