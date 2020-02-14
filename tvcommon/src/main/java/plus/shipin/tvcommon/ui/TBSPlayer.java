package plus.shipin.tvcommon.ui;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import com.tencent.smtt.sdk.VideoActivity;

import plus.shipin.tvcommon.app.Constants;

public class TBSPlayer extends VideoActivity {

    Receiver receiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new Receiver();
        IntentFilter filter = new IntentFilter(Constants.FINISHFILTER);
        this.registerReceiver(receiver,filter);

    }
    class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
