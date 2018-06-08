package com.ruanchao.ffmpegdemo.utils;

import android.os.AsyncTask;

/**
 * Created by ruanchao on 2018/6/6.
 */

public class FFmpegUtil {

    static {
        //jni库
        System.loadLibrary("native-lib");
        //ffmpeg库
        System.loadLibrary("avutil");
        System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("swresample");
        System.loadLibrary("avfilter");
    }

    public static native int ffmpegRun(String[] cmd);

    /**
     * 获取ffmpeg编译信息
     * @return
     */
    public native String getFFmpegConfig();

    /**
     * 命令运行
     * @param cmd
     * @return
     */
    public  int jxFFmpegCMDRun(String cmd){
        String regulation="[ \\t]+";
        final String[] split = cmd.split(regulation);

        return ffmpegRun(split);
    }

    public static void execute(String[] commands, final FFmpegRunListener fFmpegRunListener) {
        new AsyncTask<String[], Integer, Integer>() {
            @Override
            protected void onPreExecute() {
                if (fFmpegRunListener != null) {
                    fFmpegRunListener.onStart();
                }
            }

            @Override
            protected Integer doInBackground(String[]... params) {
                return ffmpegRun(params[0]);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (fFmpegRunListener != null) {
                    fFmpegRunListener.onEnd(integer);
                }
            }
        }.execute(commands);
    }

    public interface FFmpegRunListener{
        void onStart();
        void onEnd(int result);
    }
}
