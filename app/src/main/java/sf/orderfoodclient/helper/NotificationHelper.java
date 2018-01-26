package sf.orderfoodclient.helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import sf.orderfoodclient.R;

/**
 * Created by mesutgenc on 26.01.2018.
 */

public class NotificationHelper extends ContextWrapper {

    private static final String SF_CHANNEL_ID = "sf.orderfoodclient.SFDev";
    private static final String SF_CHANNEL_NAME = "SF";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        // only working this function if API is 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel sfChannel = new NotificationChannel(SF_CHANNEL_ID,
                SF_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        sfChannel.enableLights(false);
        sfChannel.enableVibration(true);
        sfChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(sfChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getSfChannelNotification(String title, String body,
                                                         PendingIntent contentIntent,
                                                         Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), SF_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

}
