package oleksandr.lohvinov.lab3.exoplayer.callbacks;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import oleksandr.lohvinov.lab3.exoplayer.FirebaseMusicSource;

public class MusicPlaybackPreparer implements MediaSessionConnector.PlaybackPreparer {

    private FirebaseMusicSource firebaseMusicSource;
    private PlayerPrepared playerPrepared;

    public MusicPlaybackPreparer(FirebaseMusicSource firebaseMusicSource,
                                 PlayerPrepared playerPrepared) {

        this.firebaseMusicSource = firebaseMusicSource;
        this.playerPrepared = playerPrepared;
    }

    @Override
    public long getSupportedPrepareActions() {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    @Override
    public void onPrepare(boolean playWhenReady) {

    }

    @Override
    public void onPrepareFromMediaId(String mediaId, boolean playWhenReady, @Nullable Bundle extras) {
        firebaseMusicSource.whenReady(it -> {
            MediaMetadataCompat itemToPlay = firebaseMusicSource.songs.stream().filter(s -> s.getDescription().getMediaId().equals(mediaId)).findFirst().get();
            playerPrepared.callback(itemToPlay);
        });
    }

    @Override
    public void onPrepareFromSearch(String query, boolean playWhenReady, @Nullable Bundle extras) {

    }

    @Override
    public void onPrepareFromUri(Uri uri, boolean playWhenReady, @Nullable Bundle extras) {

    }

    @Override
    public boolean onCommand(Player player, ControlDispatcher controlDispatcher, String command, @Nullable Bundle extras, @Nullable ResultReceiver cb) {
        return false;
    }
}
