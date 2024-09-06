package org.godotengine.plugin.android.jank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.stream.Stream;


public class NotificationListener extends NotificationListenerService {

    //Not fully programmed yet
    private String TAG = this.getClass().getSimpleName();
    ImageParser imageParser = new ImageParser();
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
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
                //Send notification to player
            }
        }
    }

    public void stopSelfService(){
        stopSelf();
    }
}