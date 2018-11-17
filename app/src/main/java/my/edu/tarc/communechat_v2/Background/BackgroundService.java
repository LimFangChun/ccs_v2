package my.edu.tarc.communechat_v2.Background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import my.edu.tarc.communechat_v2.internal.MqttHelper;

public class BackgroundService extends Service {
    private MqttHelper mqttHelper = new MqttHelper();
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.intent = intent;
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mqttHelper.connect(getApplicationContext());
    }
}
