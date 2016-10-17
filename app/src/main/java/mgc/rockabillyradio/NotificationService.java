package mgc.rockabillyradio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NotificationService extends Service {

    public static Context context;
    Notification status;
    boolean isPause = true;

    private void showNotification(int pos) {
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Const.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Const.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Const.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);


        if(pos == 1) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause_ntf);
        }
        if(pos == 2)
        {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.play_ntf);
        }
        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.radio;
        status.contentIntent = pendingIntent;
        startForeground(Const.FOREGROUND_SERVICE, status);
    }

    @Override
public void onDestroy() {
super.onDestroy();
}
 
@Override
public IBinder onBind(Intent intent) {
return null;
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;
        if (intent.getAction().equals(Const.ACTION.STARTFOREGROUND_ACTION)) {
            isPause = false;
            showNotification(1);
            Player.start(Const.RADIO_PATH, this);
        }
        else if (intent.getAction().equals(Const.ACTION.PLAY_ACTION)) {
            if(isPause) {
                showNotification(2);
                Player.stop();
                isPause = true;
            }
            else
            {
                showNotification(1);
                isPause = false;
                Player.start(Const.RADIO_PATH, this);
            }
        }
        else if (intent.getAction().equals(
                Const.ACTION.STOPFOREGROUND_ACTION)) {
            Player.stop();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }


}