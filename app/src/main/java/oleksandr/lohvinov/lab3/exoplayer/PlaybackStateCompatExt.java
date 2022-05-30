package oleksandr.lohvinov.lab3.exoplayer;

import android.os.SystemClock;
import android.support.v4.media.session.PlaybackStateCompat;

public class PlaybackStateCompatExt {

    public static boolean isPrepared(PlaybackStateCompat playbackStateCompat) {
        int state = playbackStateCompat.getState();

        return state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED;
    }

    public static boolean isPlaying(PlaybackStateCompat playbackStateCompat) {
        int state = playbackStateCompat.getState();

        return state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_PLAYING;
    }

    public static boolean isPlayEnabled(PlaybackStateCompat playbackStateCompat) {
        return (playbackStateCompat.getActions() & PlaybackStateCompat.ACTION_PLAY) != 0L ||
                ((playbackStateCompat.getActions() & PlaybackStateCompat.ACTION_PLAY_PAUSE) != 0L &&
                        playbackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED);
    }

    public static Long currentPlaybackPosition(PlaybackStateCompat playbackStateCompat) {
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            Long timeDelta = SystemClock.elapsedRealtime() - playbackStateCompat.getLastPositionUpdateTime();
            return (long) (playbackStateCompat.getPosition() + (timeDelta * playbackStateCompat.getPlaybackSpeed()));
        } else {
            return playbackStateCompat.getPosition();
        }
    }
}
