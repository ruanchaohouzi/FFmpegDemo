package com.ruanchao.ffmpegdemo;

import android.Manifest;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ruanchao.ffmpegdemo.utils.FFmpegUtil;
import com.ruanchao.ffmpegdemo.utils.PermissionsManager;

import java.io.File;
import java.util.regex.Pattern;

public class TestActivity extends AppCompatActivity {

    private PermissionsManager mPermissionsManager;
    private boolean isNeedCheck = true;
    public final static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private static final int PERMISSION_CODE = 100;

    private String inputPath = "/storage/emulated/0/smallvideo/video/1530844851188.mp4";
    private String outPath = "/storage/emulated/0/smallvideo/video/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mPermissionsManager = new PermissionsManager(this);
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (!mPermissionsManager.verifyPermissions(grantResults)) {
                //mPermissionsManager.showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    public void test1(View view) {
        String command = "ffmpeg -i "+ inputPath +" -vn " + outPath +"out1.mp3";
        final String command1 = "ffmpeg  -i "+inputPath+"  -vcodec copy -acodec copy -ss 00:00:10 -to 00:00:15 "+outPath+"cutout1.mp4 -y";
        String command2 = "ffmpeg -i " +inputPath+ " -vcodec copy -an "+outPath+"out3.mp4 ";
        String command4 = "ffmpeg  -i "+inputPath+" -r 10  -f gif "+outPath+"out4.gif";

        FFmpegUtil.execute(command4, new FFmpegUtil.FFmpegRunListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(int result) {

                Toast.makeText(TestActivity.this,"end", Toast.LENGTH_LONG).show();
            }
        });

        String str = "abcrwerw";
        //字符串中是否包含字符串
        String regex = ".*abc*.";

        boolean matches = Pattern.matches(regex, str);
    }
}
