package plus.shipin.tvcommon.app;

import android.app.Application;

import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.umeng.commonsdk.UMConfigure;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
                .setPlayerFactory(IjkPlayerFactory.create())
                //使用ExoPlayer解码
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                //使用MediaPlayer解码
//                .setPlayerFactory(AndroidMediaPlayerFactory.create())
                .build());
        ZXingLibrary.initDisplayOpinion(this);
        UMConfigure.init(this, "5e440f77cb23d21b5c000068", "db",0, null);
    }
}
