package com.ruanchao.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.Log;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.BaseMediaBitrateConfig;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.ruanchao.demo.event.FinishVideoMsgEvent;
import com.ruanchao.demo.videorecord.Constans;
import com.ruanchao.demo.videorecord.FinishSmallVideoActivity;
import com.ruanchao.demo.videorecord.VideoAdapter;
import com.ruanchao.demo.videorecord.VideoInfo;
import com.ruanchao.demo.videorecord.VideoPlayActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 0x001;

    private RecyclerView mRecycler;
    private VideoAdapter mVideoAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initSmallVideo();
        initView();
        permissionCheck();

    }

    private void initView() {
        mRecycler = (RecyclerView) findViewById(R.id.recycler_live_video);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divicer));
        mRecycler.addItemDecoration(divider);
        mVideoAdapter = new VideoAdapter(this);
        mVideoAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(VideoInfo VideoInfo) {
                VideoPlayActivity.start(MainActivity.this, VideoPlayActivity.class, VideoInfo.getVideoPath());
            }
        });
        mRecycler.setAdapter(mVideoAdapter);
    }

    public void go(View c) {

        String width = "360";
        String height = "480";
        String maxFramerate = "20";
        String bitrate = "580000";
        String maxTime = "10000";
        String minTime = "3000";
        boolean needFull = false;
        BaseMediaBitrateConfig recordMode;
        BaseMediaBitrateConfig compressMode = null;
        recordMode = new AutoVBRMode();
        recordMode.setVelocity("ultrafast");
//      FFMpegUtils.captureThumbnails("/storage/emulated/0/DCIM/mabeijianxi/1496455533800/1496455533800.mp4", "/storage/emulated/0/DCIM/mabeijianxi/1496455533800/1496455533800.jpg", "1");

        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .fullScreen(needFull)
                .smallVideoWidth(needFull?0:Integer.valueOf(width))
                .smallVideoHeight(Integer.valueOf(height))
                .recordTimeMax(Integer.valueOf(maxTime))
                .recordTimeMin(Integer.valueOf(minTime))
                .maxFrameRate(Integer.valueOf(maxFramerate))
                .videoBitrate(Integer.valueOf(bitrate))
                .captureThumbnailsTime(1)
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(this, FinishSmallVideoActivity.class.getName(), config);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
            getLiveVideoList();
        }
    }


    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean permissionState = true;
            for (String permission : permissionManifest) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionState = false;
                }
            }
            if (!permissionState) {
                ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
            } else {
                getLiveVideoList();
            }
        } else {
            getLiveVideoList();
        }
    }

    public static void initSmallVideo() {
        File videoFile = new File(Constans.VIDEO_TEMP_PATH);
        if (!videoFile.exists()){
            videoFile.mkdirs();
        }
        JianXiCamera.setVideoCachePath(Constans.VIDEO_TEMP_PATH);
        // 初始化拍摄
        JianXiCamera.initialize(true, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishVideoMsgEvent event) {
        Log.i(TAG, "onMessageEvent FinishVideoMsgEvent");
        mVideoAdapter.addVideoInfoFront(event.videoInfo);
        mVideoAdapter.notifyDataSetChanged();
    };

    public void getLiveVideoList(){
        Observable.create(new Observable.OnSubscribe<List<VideoInfo>>() {
            @Override
            public void call(Subscriber<? super List<VideoInfo>> subscriber) {
                List<VideoInfo> list = parseLiveVideoFile();
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VideoInfo>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<VideoInfo> videoInfos) {
                        mVideoAdapter.addVideoInfos(videoInfos);
                    }
                });
    }

    private List<VideoInfo> parseLiveVideoFile() {
        File file = new File(Constans.VIDEO_PATH);
        if (!file.isDirectory()){
            return null;
        }
        File[] videoFiles = file.listFiles();
        List<VideoInfo> list = new ArrayList<>();
        for (File videoFile : videoFiles){
            if(videoFile.exists() && videoFile.getName().endsWith(".mp4")){
                VideoInfo liveVideoInfo = new VideoInfo();
                liveVideoInfo.setVideoPath(videoFile.getAbsolutePath());
                liveVideoInfo.setVideoTitle(videoFile.getName());
                liveVideoInfo.setVideoTime(videoFile.lastModified());
                list.add(liveVideoInfo);
            }
        }
        Collections.sort(list);
        return list;
    }


}
