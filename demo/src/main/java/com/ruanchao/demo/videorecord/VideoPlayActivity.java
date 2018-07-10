package com.ruanchao.demo.videorecord;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;


import com.ruanchao.demo.R;

import java.util.Random;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String VIDEO_PATH = "video_path";
    private static final int WHAT_ADD_DANMU = 100;
    private Random mRandom;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_ADD_DANMU:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(null);
    }

    private void init() {
        VideoView mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setMediaController(new MediaController(this));
        Uri videoUri = Uri.parse(getIntent().getStringExtra(VIDEO_PATH));
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();
        mRandom = new Random();
    }

    public static void start(Context context, Class clazz, String videoPath){
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(VIDEO_PATH,videoPath);
        context.startActivity(intent);
    }
}
