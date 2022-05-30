package oleksandr.lohvinov.lab3.exoplayer.callbacks;

import static oleksandr.lohvinov.lab3.other.Constants.NOTIFICATION_ID;

import android.app.Notification;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import oleksandr.lohvinov.lab3.exoplayer.MusicService;

public class MusicPlayerNotificationListener implements PlayerNotificationManager.NotificationListener {
    private MusicService musicService;

    public MusicPlayerNotificationListener(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
        musicService.stopForeground(true);
        musicService.isForegroundService = false;
        musicService.stopSelf();
    }

    @Override
    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
        if(ongoing && !musicService.isForegroundService){
            ContextCompat.startForegroundService(musicService,
                    new Intent(musicService.getApplicationContext(),
                            musicService.getClass()));

            musicService.startForeground(NOTIFICATION_ID, notification);
            musicService.isForegroundService = true;
        }
    }
}
