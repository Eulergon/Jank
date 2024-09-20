package org.godotengine.plugin.android.jank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.stream.Stream;


public class NotificationListener extends NotificationListenerService {

    ImageParser imageParser = new ImageParser();
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.org.godotengine.plugin.android.jank");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(sbn.getPackageName() == "com.whatsapp" && sbn.getNotification().hasImage()){
            ImageParser.GameInfo gameInfo = imageParser.decodePNG();
            if(Stream.of(gameInfo.getName(), gameInfo.getGameID(), gameInfo.getLocalGameID(), gameInfo.getPfp(), gameInfo.getMoveMade()).allMatch(x -> x != null)){
               NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("A Player Has Requested A Game/Made Their Move!")
                        .setContentText(gameInfo.getName() + "has requested to play")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return super.onBind(intent);
    }

    public void stopSelfService(){
        stopSelf();
    }
}