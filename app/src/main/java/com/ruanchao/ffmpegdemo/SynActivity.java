package com.ruanchao.ffmpegdemo;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ruanchao.ffmpegdemo.utils.FFmpegCommands;
import com.ruanchao.ffmpegdemo.utils.FFmpegUtil;
import com.ruanchao.ffmpegdemo.utils.FileUtils;

import java.io.IOException;

public class SynActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SynActivity";
    private Button mChooseMusic;
    private Button mSynVideo;
    private String mTargetPath;
    FileUtils mFileUtils;
    MediaPlayer mAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syn);
        findViewById(R.id.et_music);
        mSynVideo = findViewById(R.id.btn_syn_video);
        mSynVideo.setOnClickListener(this);
        mFileUtils = new FileUtils(this);
        mTargetPath = mFileUtils.getStorageDirectory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_syn_video:
                composeVideoAudio();
                break;
                default:
                    break;
        }
    }

    private void composeVideoAudio() {

    }


}
