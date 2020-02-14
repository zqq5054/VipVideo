package plus.shipin.tvcommon.ui;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.danikula.videocache.HttpProxyCacheServer;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.player.VideoView;

import plus.shipin.tvcommon.R;
import plus.shipin.tvcommon.utils.ProxyVideoCacheManager;


public class PlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        videoView = findViewById(R.id.player);
        String url = getIntent().getStringExtra("url");
        HttpProxyCacheServer cacheServer = ProxyVideoCacheManager.getProxy(this);
        String proxyUrl = cacheServer.getProxyUrl(url);
        videoView.setUrl(url); //设置视频地址

        StandardVideoController controller = new StandardVideoController(this);
        controller.setEnableOrientation(true);
        GestureView gestureControlView = new GestureView(this);//滑动控制视图
        controller.addControlComponent(gestureControlView);
        controller.addControlComponent(new CompleteView(this));//自动完成播放界面
        controller.addControlComponent(new ErrorView(this));//错误界面
        TitleView titleView = new TitleView(this);//标题栏
        VodControlView vodControlView = new VodControlView(this);//点播控制条
        //是否显示底部进度条。默认显示
//                vodControlView.showBottomProgress(false);
        controller.addControlComponent(vodControlView);
        controller.addControlComponent(titleView);
        videoView.setVideoController(controller); //设置控制器
        videoView.start(); //开始播放，不调用则不自动播放
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!videoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String url = intent.getStringExtra("url");
        System.out.println("new instance url = "+url);
        videoView.release();
        videoView.setUrl(url); //设置视频地址
        videoView.start(); //开始播放，不调用则不自动播放
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){

            case KeyEvent.KEYCODE_DPAD_CENTER:
                if(videoView.isPlaying()) {
                    videoView.pause();
                }else{
                    videoView.start();
                }
                break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    videoView.seekTo(videoView.getCurrentPosition()-60000);
                    break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                videoView.seekTo(videoView.getCurrentPosition()+60000);

                break;

        }

        return super.onKeyDown(keyCode, event);
    }
}
