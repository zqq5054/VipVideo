package plus.shipin.tvcommon.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.fyales.tagcloud.library.TagBaseAdapter;
import com.fyales.tagcloud.library.TagCloudLayout;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.vector.update_app.UpdateAppManager;

import java.util.ArrayList;
import java.util.List;

import plus.shipin.tvcommon.R;
import plus.shipin.tvcommon.app.Constants;
import plus.shipin.tvcommon.utils.UpdateAppHttpUtil;

public class PlatformActivity extends AppCompatActivity {

    private TagCloudLayout mContainer;
    private TagBaseAdapter mAdapter;
    private static final int REQUEST_CODE = 1024;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);
        reqUpdate();
        mContainer = (TagCloudLayout) findViewById(R.id.container);
        List mList = new ArrayList<>();
        mList.add("腾讯视频(1)");
        mList.add("爱奇艺(2)");
        mList.add("优酷(3)");
        mList.add("搜狐(4)");
        mList.add("乐视(5)");
        mList.add("芒果TV(6)");
        mList.add("扫码连接电视");
        mList.add("电视请点我或按菜单键");
        mList.add("手机端下载(仅支持安卓)(7)");
        requestSD();
        mAdapter = new TagBaseAdapter(this,mList);
        mContainer.setAdapter(mAdapter);
        mContainer.setItemClickListener(new TagCloudLayout.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                switch (position){
                    case 0:
                        gotoSite("http://v.qq.com");
                        break;
                    case 1:
                        gotoSite("http://iqiyi.com");
                        break;
                    case 2:
                        gotoSite("http://youku.com");
                        break;
                    case 3:
                        gotoSite("http://tv.sohu.com/");
                        break;
                    case 4:
                        gotoSite("http://www.le.com/");
                        break;
                    case 5:
                        gotoSite("https://www.mgtv.com/");
                        break;
                    case 6:
                        requestCemera();
                        break;
                    case 7:
                        goTv();
                        break;
                    case 8:
                        showPhone();
                        break;

                }
            }
        });
    }


    private void gotoSite(String url){

        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);


    }
    private void requestCemera() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Intent intent = new Intent(PlatformActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(PlatformActivity.this, "扫码需要您授权访问摄像头", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
    }
    private void requestSD() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {

            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(PlatformActivity.this, "缓存需要访问您的存储", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_MENU){

            goTv();
        }else if(keyCode == KeyEvent.KEYCODE_1){
            gotoSite("http://v.qq.com");
        }else if(keyCode == KeyEvent.KEYCODE_2){
            gotoSite("http://iqiyi.com");
        }else if(keyCode == KeyEvent.KEYCODE_3){
            gotoSite("http://youku.com");
        }else if(keyCode == KeyEvent.KEYCODE_4){
            gotoSite("http://tv.sohu.com/");
        }else if(keyCode == KeyEvent.KEYCODE_5){
            gotoSite("http://www.le.com/");
        }else if(keyCode == KeyEvent.KEYCODE_6){
            gotoSite("https://www.mgtv.com/");
        }else if(keyCode == KeyEvent.KEYCODE_7){
            showPhone();
        }


        return super.onKeyDown(keyCode, event);
    }

    private void reqUpdate(){

        //最简方式
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(Constants.HOST+"/VipPlayer/api/update")
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .update();
    }
    private void goTv(){

        Intent intent = new Intent(this,TVActivity.class);
        startActivity(intent);
    }

    private void showPhone(){

        ImageView imv = new ImageView(this);
        imv.setImageResource(R.drawable.app);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("客户端下载");
        builder.setView(imv);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if(!result.startsWith("serverIp")){
                        Toast.makeText(this, "二维码不正确", Toast.LENGTH_LONG).show();
                    }else{
                        String ip = result.split("-")[1];
                        SharedPreferences sharedPreferences = getSharedPreferences("data",0);
                        sharedPreferences.edit().putString("ip",ip).commit();
                        Toast.makeText(this, "绑定成功", Toast.LENGTH_LONG).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
