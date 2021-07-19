package com.example.GGmusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service {
    private static final int ONGOING_NOTIFICATION_ID=1001;
    private static final String CHANNERL_ID="Music Channel";
    NotificationManager mNotificationMannager;
    private MediaPlayer mMediaplayer;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaplayer=new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        mMediaplayer.stop();
        mMediaplayer.release();
        mMediaplayer=null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title=intent.getStringExtra(MainActivity.TITLE);
        String artist=intent.getStringExtra(MainActivity.ARTIST);
        String data=intent.getStringExtra(MainActivity.DATA_URL);
        Uri dataUri=Uri.parse(data);
        if(mMediaplayer!=null)
        {

            try {
                mMediaplayer.reset();
                mMediaplayer.setDataSource(getApplicationContext(),dataUri);
                mMediaplayer.prepare();
                mMediaplayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            mNotificationMannager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel=new NotificationChannel(CHANNERL_ID,"Music Channel",NotificationManager.IMPORTANCE_HIGH);
            if(mNotificationMannager!=null)
            {
                mNotificationMannager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent=new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),CHANNERL_ID);
        Notification notification=builder.setContentTitle(title).setContentText(artist).setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();
        startForeground(ONGOING_NOTIFICATION_ID,notification);
        return super.onStartCommand(intent, flags, startId);
    }
}