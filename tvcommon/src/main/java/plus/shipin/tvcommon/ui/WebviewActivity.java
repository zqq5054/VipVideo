package plus.shipin.tvcommon.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

import plus.shipin.tvcommon.R;

public class WebviewActivity extends AppCompatActivity implements Runnable{

    private com.tencent.smtt.sdk.WebView webView;
    private boolean isPause = false;
    private String analysisJs = null;
    private String site = "https://api.sigujx.com/jx/?url=";
    private com.tencent.smtt.sdk.WebView analysis;
    Map<String,String> header = null;
    private long startAna = -1;
    private ProgressDialog pd;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.webView);
        analysis = findViewById(R.id.analysis);
        String url = getIntent().getStringExtra("url");

        findViewById(R.id.exit).setOnClickListener(view->{
            finish();
        });
        findViewById(R.id.analysisBtn).setOnClickListener(view->{
            analysisUrl();
        });
        com.tencent.smtt.sdk.WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.addJavascriptInterface(new Javascript(), "injectedObject");
        analysis.getSettings().setJavaScriptEnabled(true);

        analysis.getSettings().setJavaScriptEnabled(true);
        analysis.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        analysis.getSettings().setUseWideViewPort(true);
//        analysis.getSettings().setLoadWithOverviewMode(true);
        analysis.getSettings().setDefaultTextEncodingName("utf-8");
        analysis.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
       analysis.getSettings().setUserAgentString("User-Agent,Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");

        analysis.addJavascriptInterface(new AnalysisJavascript(), "injectedObject");
        webView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {

                if(url.startsWith("http")) {
                    view.loadUrl(url);// 强制在当前 WebView 中加载 url
                }
                return true;
            }
        });

        analysis.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);// 强制在当前 WebView 中加载 url
                return true;
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();
                if(url.contains(".m3u8")||url.contains(".mp4")){
                    if(!isPause) {
                        if(TbsVideo.canUseTbsPlayer(WebviewActivity.this)) {
                            Intent intent = new Intent(WebviewActivity.this, TBSPlayer.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("videoUrl", url);
                            bundle.putInt("screenMode", 102);
                            intent.putExtra("extraData", bundle);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(WebviewActivity.this,PlayerActivity.class);
                            intent.putExtra("url",url);
                            startActivity(intent);
                        }
//                        Bundle extraData = new Bundle();
//                        extraData.putInt("screenMode",102);
//                        System.out.println("url = "+url);
//                        TbsVideo.openVideo(WebviewActivity.this,url,extraData);
                    }
                    pd.dismiss();
                    thread.interrupt();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences("data",0);
                            String ip = sharedPreferences.getString("ip",null);
                            if(ip==null) {
                                Toast.makeText(WebviewActivity.this, "解析成功,即将开始播放,电视尚未连接", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(WebviewActivity.this, "解析成功,即将开始播放,并同步推送到电视", Toast.LENGTH_SHORT).show();
                                push(ip,url);
                            }
                            analysis.loadUrl("http://www.jspoo.com/");
//                            analysis.pauseTimers();
//                            analysis.stopLoading();
                        }
                    });

                }
                if(url.endsWith(".ts")){
                    return null;
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

    }

    /**
     * 把解析出来的视频地址推送到电视上
     * @param ip 电视的ip
     * @param url 视频url
     */
    private void push(String ip,String url){

        try {
             url = Base64.encodeToString(url.getBytes("UTF-8"),Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }

        String uri = "http://"+ip+":8081/pushVideo?url="+url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connect = Jsoup.connect(uri).validateTLSCertificates(false)
                        .ignoreContentType(true)
                        .timeout(5000);
                try {
                    connect.get();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }



    final class Javascript{ // 也可以在视频网站的页面，通过注入JS方式回调解析，解析按钮可以和视频网站页面融合在一起，视觉上更好看

        @JavascriptInterface
        public void analysis() {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    analysisUrl();
                }
            });
        }

    }

    private void analysisUrl() {

            if(pd == null){
                pd = new ProgressDialog(WebviewActivity.this);
                pd.setMessage("解析中,请稍候...");
                pd.setCancelable(false);
               thread =  new Thread(WebviewActivity.this);
               thread.start();
               pd.show();
            }else{
                if(pd.isShowing()){
                    return;
                }else{
                    thread =  new Thread(WebviewActivity.this);
                    thread.start();
                    pd.show();
                }
            }
            if(header==null){
                header = new HashMap<>();
                header.put("Referer","https://api.sigujx.com/jx/?url="+webView.getUrl());
            }
            if(TextUtils.isEmpty(analysisJs)){
                if(header == null) {
                    analysis.loadUrl(site + webView.getUrl());
                }else{
                    analysis.loadUrl(site + webView.getUrl(),header);
                }
            }

    }

    final class AnalysisJavascript{

        @JavascriptInterface
        public void analysis(String html) {



        }
    }

    @Override
    public void run() {  //30秒超时
        startAna = System.currentTimeMillis();
        boolean isTimeOut = false;
        while (!isTimeOut) {

            try {
                Thread.sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(System.currentTimeMillis()-startAna>=30000){
                isTimeOut = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pd.isShowing()) {
                            analysis.loadUrl("javascript:window.injectedObject.analysis(document.getElementsByTagName('html')[0].innerHTML);");
                            pd.dismiss();
                            Toast.makeText(WebviewActivity.this, "超时，请重试", Toast.LENGTH_SHORT).show();
                            analysis.loadUrl("http://www.jspoo.com/");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }
}
