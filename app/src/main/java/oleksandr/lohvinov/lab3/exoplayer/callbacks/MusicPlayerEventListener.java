package oleksandr.lohvinov.lab3.exoplayer.callbacks;

import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;

import oleksandr.lohvinov.lab3.exoplayer.MusicService;

public class MusicPlayerEventListener implements Player.EventListener {
    private MusicService musicService;

    public MusicPlayerEventListener(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playbackState == Player.STATE_READY && !playWhenReady){
            musicService.stopForeground(false);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(musicService, "An unknown error occurred", Toast.LENGTH_LONG).show();
    }
}
