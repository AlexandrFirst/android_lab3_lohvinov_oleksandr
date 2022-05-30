package oleksandr.lohvinov.lab3.exoplayer;

import static oleksandr.lohvinov.lab3.other.Constants.MEDIA_ROOT_ID;
import static oleksandr.lohvinov.lab3.other.Constants.NETWORK_ERROR;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import oleksandr.lohvinov.lab3.exoplayer.callbacks.MusicPlaybackPreparer;
import oleksandr.lohvinov.lab3.exoplayer.callbacks.MusicPlayerEventListener;
import oleksandr.lohvinov.lab3.exoplayer.callbacks.MusicPlayerNotificationListener;

@AndroidEntryPoint
public class MusicService extends MediaBrowserServiceCompat {

    private static final String SERVICE_TAGE = "MusicService";
    @Inject
    DefaultDataSourceFactory dataSourceFactory;

    @Inject
    SimpleExoPlayer exoPlayer;

    @Inject
    FirebaseMusicSource firebaseMusicSource;

    private MusicNotificationManager musicNotificationManager;

    ExecutorService executor = Executors.newFixedThreadPool(2);

    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;

    public boolean isForegroundService = false;

    private MediaMetadataCompat curPlayingSong = null;

    private boolean isPlayerInitialized = false;

    private MusicPlayerEventListener musicPlayerEventListener;

    private static long curSongDuration = 0L;
    public static long getCurSongDuration(){
        return curSongDuration;
    }
    private void setCurSongDuration(long duration){
        curSongDuration = duration;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        executor.submit(new Runnable() {
            @Override
            public void run() {
                firebaseMusicSource.fetchMediaData();
            }
        });

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

        mediaSession = new MediaSessionCompat(this, SERVICE_TAGE);
        mediaSession.setSessionActivity(activityIntent);
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken());
        musicNotificationManager = new MusicNotificationManager(this, mediaSession.getSessionToken(),
                new MusicPlayerNotificationListener(this), () -> {
            setCurSongDuration(exoPlayer.getDuration());
        });

        MusicPlaybackPreparer musicPlaybackPreparer = new MusicPlaybackPreparer(firebaseMusicSource, (it) -> {
            curPlayingSong = it;
            preparePlayer(firebaseMusicSource.songs, it, true);
        });


        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer);
        mediaSessionConnector.setQueueNavigator(new MusicQueueNavigator(mediaSession));
        mediaSessionConnector.setPlayer(exoPlayer);

        musicPlayerEventListener = new MusicPlayerEventListener(this);

        exoPlayer.addListener(musicPlayerEventListener);
        musicNotificationManager.showNotification(exoPlayer);
    }

    private class MusicQueueNavigator extends TimelineQueueNavigator {

        public MusicQueueNavigator(MediaSessionCompat mediaSession) {
            super(mediaSession);
        }

        @Override
        public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
            return firebaseMusicSource.songs.get(windowIndex).getDescription();
        }
    }

    private void preparePlayer(List<MediaMetadataCompat> songs,
                               MediaMetadataCompat itemToPlay,
                               boolean playNow) {
        int curSongIndex = -1;
        if (curPlayingSong == null) {
            curSongIndex = 0;
        } else {
            curSongIndex = songs.indexOf(itemToPlay);
        }
        exoPlayer.prepare(firebaseMusicSource.asMediaSource(dataSourceFactory));
        exoPlayer.seekTo(curSongIndex, 0L);
        exoPlayer.setPlayWhenReady(playNow);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        switch (parentId) {
            case MEDIA_ROOT_ID:
                boolean resultSent = firebaseMusicSource.whenReady(isInitialized -> {
                    if (isInitialized == true) {
                        result.sendResult(firebaseMusicSource.asMediaItems());
                        if (isPlayerInitialized == false && !firebaseMusicSource.songs.isEmpty()) {
                            preparePlayer(firebaseMusicSource.songs, firebaseMusicSource.songs.get(0), false);
                            isPlayerInitialized = true;
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null);
                        result.sendResult(null);
                    }
                });

                if (!resultSent) {
                    result.detach();
                }
                break;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        exoPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        exoPlayer.removeListener(musicPlayerEventListener);
        exoPlayer.release();
    }
}
