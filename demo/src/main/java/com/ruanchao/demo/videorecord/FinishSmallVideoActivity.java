package com.ruanchao.demo.videorecord;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mabeijianxi.smallvideorecord2.Log;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.jniinterface.FFmpegBridge;
import com.ruanchao.demo.R;
import com.ruanchao.demo.event.FinishVideoMsgEvent;
import com.ruanchao.demo.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FinishSmallVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private String tempVideoUri;
    private String tempTransformVideoUri;
    private String finishVideoUri;
    private TextView tv_finish;
    private TextView tv_cancel;
    private String videoScreenshot;
    private ImageView iv_video_screenshot;
    private TextView et_send_content;
    private static final String TAG = FinishSmallVideoActivity.class.getSimpleName();
    private AlertDialog dialog;
    private Button mReplaceBgm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initEvent() {
        tv_cancel.setOnClickListener(this);
        tv_finish.setOnClickListener(this);
        et_send_content.setOnClickListener(this);
        iv_video_screenshot.setOnClickListener(this);
    }


    private void initData() {
        Intent intent = getIntent();
        tempVideoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
        videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);
        Bitmap bitmap = BitmapFactory.decodeFile( videoScreenshot);
        iv_video_screenshot.setImageBitmap(bitmap);
        et_send_content.setText("您视频地址为:"+ tempVideoUri);
    }

    private void initView() {
        setContentView(R.layout.smallvideo_text_edit_activity);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        et_send_content = (TextView) findViewById(R.id.et_send_content);
        iv_video_screenshot = (ImageView) findViewById(R.id.iv_video_screenshot);
        mReplaceBgm = findViewById(R.id.btn_replace_bgm);
        mReplaceBgm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                hesitate();
                break;
            case R.id.tv_finish:
                moveVideoToPath();
                finish();
                //发送通知
                sendFinishEvent();
                break;
            case R.id.iv_video_screenshot:
                VideoPlayActivity.start(this, VideoPlayActivity.class, tempVideoUri);
                break;
            case R.id.btn_replace_bgm:
                replaceBgm();
                break;
                default:
                    break;
        }
    }

    private void replaceBgm() {
        File tempVideoFile = new File(tempVideoUri);
        tempTransformVideoUri = tempVideoFile.getParent() + File.separator + System.currentTimeMillis() + ".mp4";
        final StringBuilder sb = new StringBuilder();
        sb.append("ffmpeg -y  -i " + tempVideoUri);
        sb.append("  -f lavfi -i amovie=" + Constans.MUSIC_PATH + File.separator + "Honor.mp3");
        sb.append(" -filter_complex \"[0:a]volume=1[a0]; [1:a]volume=2[a1]; [a0][a1]amix=inputs=2:duration=first[aout]\"");
        sb.append(" -map \"[aout]\" -preset ultrafast  -ac 2 -c:v copy -map 0:v:0  " +  tempTransformVideoUri);
        Log.i(TAG, "replaceBgm:" + sb.toString());

        final String gifCMD = "ffmpeg -i "+ tempVideoUri +" -s 360x480 -r 10 " + tempVideoFile.getParent() + "/test.gif";


        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int result = FFmpegBridge.jxFFmpegCMDRun(gifCMD);
                subscriber.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onStart() {
                super.onStart();
                Toast.makeText(FinishSmallVideoActivity.this,"处理中，请稍后", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(FinishSmallVideoActivity.this,"添加背景音乐失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(Integer integer) {
                Toast.makeText(FinishSmallVideoActivity.this,"添加背景音乐成功", Toast.LENGTH_LONG).show();
                tempVideoUri = tempTransformVideoUri;
            }
        });

    }

    private void sendFinishEvent() {
        File videoFile = new File(finishVideoUri);
        if (videoFile.exists()) {
            FinishVideoMsgEvent finishVideoMsgEvent = new FinishVideoMsgEvent();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setVideoPath(videoFile.getAbsolutePath());
            videoInfo.setVideoTitle(videoFile.getName());
            videoInfo.setVideoTime(videoFile.lastModified());
            finishVideoMsgEvent.videoInfo = videoInfo;
            Log.i(TAG, "onMessageEvent post FinishVideoMsgEvent");
            EventBus.getDefault().post(finishVideoMsgEvent);
            Log.i(TAG, "onMessageEvent post FinishVideoMsgEvent end");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void moveVideoToPath() {
        File videoFile = new File(tempVideoUri);
        if (videoFile.exists()) {
            String destDir = Constans.VIDEO_PATH;
            FileUtil.copyFileToDir(tempVideoUri, destDir);
            FileUtil.deleteFile(videoFile.getParentFile());
            finishVideoUri = destDir + File.separator + videoFile.getName();
        }
    }


    @Override
    public void onBackPressed() {
        hesitate();
    }

    private void hesitate() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    FileUtil.deleteFile((new File(tempVideoUri)).getParentFile());

                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
        } else {
            dialog.show();
        }
    }

}
