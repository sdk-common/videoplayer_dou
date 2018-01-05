package com.dou.samples.mediaplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Dou on 2018/1/5.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomMediaplayer player = findViewById(R.id.mediaplayer);
        CustomMediaController controller = new CustomMediaController(MainActivity.this);
        controller.setTitle("芳华");
        player.setController(controller);
        // "http://flv3.bn.netease.com/videolib3/1801/05/wLzFE0523/SD/wLzFE0523-mobile.mp4"
        player.setUp("android.resource://"+getPackageName()+"/" + R.raw.test_mp4);
        player.start();
    }
}
