package com.ruanchao.ffmpegdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ruanchao.ffmpegdemo.utils.FFmpegUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.sample_text);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "111.mp4";
//        int rotation = FFmpegUtil.getRotation(path);
//        tv.setText(String.valueOf(rotation));
    }

}
