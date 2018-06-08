package com.ruanchao.ffmpegdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruanchao.ffmpegdemo.utils.FFmpegCommands;
import com.ruanchao.ffmpegdemo.utils.FFmpegUtil;
import com.ruanchao.ffmpegdemo.utils.FileUtils;
import com.ruanchao.ffmpegdemo.utils.MediaHelper;
import com.ruanchao.ffmpegdemo.utils.PermissionsManager;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private MediaHelper mMediaHelper;
    private PermissionsManager mPermissionsManager;
    private boolean isNeedCheck = true;
    public final static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private static final int PERMISSION_CODE = 100;
    private FileUtils mFileUtils;
    private ImageView mStart;
    private boolean isStarting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mPermissionsManager = new PermissionsManager(this);
        initView();
    }

    private void initView() {
        mStart = findViewById(R.id.iv_start);
        mStart.setOnClickListener(this);
        mSurfaceView = findViewById(R.id.sv_camera_view);
        mMediaHelper = new MediaHelper(this);
        //录制之前删除所有的多余文件
        mFileUtils = new FileUtils(this);
        mFileUtils.deleteFile(mFileUtils.getMediaVideoPath(),null);
        mFileUtils.deleteFile(mFileUtils.getStorageDirectory(),null);
        mMediaHelper.setTargetDir(new File(mFileUtils.getMediaVideoPath()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
            if (isNeedCheck && mPermissionsManager.checkPermissions(PERMISSION_CODE, PERMISSIONS)){
                return;
            }
        }
        mMediaHelper.setSurfaceView(mSurfaceView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (!mPermissionsManager.verifyPermissions(grantResults)) {
                //mPermissionsManager.showMissingPermissionDialog();
                isNeedCheck = false;
            }else {
                mMediaHelper.setSurfaceView(mSurfaceView);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_start:
                startAndStopRecord();
                break;
                default:
                    break;
        }
    }

    private void startAndStopRecord() {
        if (!isStarting){
            isStarting = true;
            mMediaHelper.record();
            mStart.setImageResource(R.mipmap.icon_video_ing);
        }else {
            isStarting = false;
            mStart.setImageResource(R.mipmap.bt_start);
            mMediaHelper.stopRecordSave();
            String path = mMediaHelper.getTargetFilePath();
            Intent intent = new Intent(this,SynActivity.class);
            intent.putExtra("path",path);
            startActivity(intent);
        }
    }

    /**
     * 提取音频
     */
    public void extractAudio(String videoUrl, String outUrl, FFmpegUtil.FFmpegRunListener listener){
        String[] commands = FFmpegCommands.extractAudio(videoUrl, outUrl);
        FFmpegUtil.execute(commands, listener);
    }
    /**
     * 提取视频
     */
    public void extractVideo(String videoUrl, String outUrl, FFmpegUtil.FFmpegRunListener listener){

    }

    /**
     * 处理背景音乐
     */
    private void composeVideoMusic(final String audioUrl,String audioOrMusicUrl, int mMusicVol, String musicOutUrl, FFmpegUtil.FFmpegRunListener listener) {
        final String[] common = FFmpegCommands.changeAudioOrMusicVol(audioOrMusicUrl, mMusicVol , musicOutUrl);
        FFmpegUtil.execute(common, listener);
    }

    /**
     * 合成音视频
     */
    public void composeAudioAndMusic(String audioUrl, String musicUrl, String musicAudioPath, FFmpegUtil.FFmpegRunListener listener) {
        String[] common = FFmpegCommands.composeAudio(audioUrl, musicUrl, musicAudioPath);
        FFmpegUtil.execute(common, listener);

    }
}
