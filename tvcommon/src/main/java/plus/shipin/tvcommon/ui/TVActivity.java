package plus.shipin.tvcommon.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.tencent.smtt.sdk.TbsVideo;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import plus.shipin.tvcommon.R;
import plus.shipin.tvcommon.app.Constants;

public class TVActivity extends AppCompatActivity {

    private ImageView serverIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        serverIp = findViewById(R.id.serverIp);
        Bitmap mBitmap = CodeUtils.createImage("serverIp-"+getLocalIpAddress(), 600, 600, null);
        serverIp.setImageBitmap(mBitmap);
        AsyncHttpServer server = new AsyncHttpServer();
        server.get("/pushVideo", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                response.send("success");
                response.end();
                String url = request.getQuery().getString("url");
                try {
                    url = new String(Base64.decode(url, Base64.DEFAULT), "UTF-8");
                }catch (Exception e){
                    e.printStackTrace();
                }
//                Bundle extraData = new Bundle();
//                extraData.putInt("screenMode",102);
//                System.out.println("url = "+url);
//                TbsVideo.openVideo(TVActivity.this,url);
                if(TbsVideo.canUseTbsPlayer(TVActivity.this)) {
                    Intent fin = new Intent(Constants.FINISHFILTER);
                    sendBroadcast(fin);
                    Intent intent = new Intent(TVActivity.this, TBSPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("videoUrl", url);
                    bundle.putInt("screenMode", 102);
                    intent.putExtra("extraData", bundle);
                    startActivity(intent);
                }else{

                    Intent intent = new Intent(TVActivity.this,PlayerActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TVActivity.this,"内核不支持，将调用系统播放器解码",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        server.listen(8081);

    }
    /**
     * 获取当前ip地址
     * @return
     */
    public  String getLocalIpAddress() {
        try {

            WifiManager wifiManager = (WifiManager) this.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
        // return null;
    }
    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }
}
