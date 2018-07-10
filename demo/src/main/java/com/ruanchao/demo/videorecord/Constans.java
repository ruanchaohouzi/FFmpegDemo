package com.ruanchao.demo.videorecord;

import android.os.Environment;

import java.io.File;

/**
 * Created by ruanchao on 2018/7/5.
 */

public class Constans {

    public static final String SDCARD = Environment
            .getExternalStorageDirectory().getAbsoluteFile() + File.separator;
    public static final String FFMPEG_PATH = SDCARD + "smallvideo" + File.separator;

    // 设置拍摄视频临时路径
    public static final String VIDEO_TEMP_PATH = FFMPEG_PATH + "temp" + File.separator;

    // 设置拍摄视频路径
    public static final String VIDEO_PATH = FFMPEG_PATH + "video";

    // 设置背景音乐路径
    public static final String MUSIC_PATH = FFMPEG_PATH + "music";
}
