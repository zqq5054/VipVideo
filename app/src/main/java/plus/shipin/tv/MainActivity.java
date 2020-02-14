package plus.shipin.tv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import plus.shipin.tvcommon.ui.PlayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.test).setOnClickListener(view->{

            Intent intent = new Intent(this,PlayerActivity.class);
            intent.putExtra("url","http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
            startActivity(intent);
        });
    }
}
