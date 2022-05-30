package oleksandr.lohvinov.lab3.ui.viewmodels;

import static oleksandr.lohvinov.lab3.other.Constants.UPDATE_PLAYER_POSITION_INTERVAL;

import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import oleksandr.lohvinov.lab3.exoplayer.MusicService;
import oleksandr.lohvinov.lab3.exoplayer.MusicServiceConnection;
import oleksandr.lohvinov.lab3.exoplayer.PlaybackStateCompatExt;

@HiltViewModel
public class SongViewModel extends ViewModel {

    private MutableLiveData<PlaybackStateCompat> playbackState;

    private MutableLiveData<Long> _curSongDuration = new MutableLiveData<>();

    public LiveData<Long> curSongDuration() {
        return _curSongDuration;
    }

    private MutableLiveData<Long> _curPlayerPosition = new MutableLiveData<>();

    public LiveData<Long> curPlayerPosition() {
        return _curPlayerPosition;
    }

    @Inject
    public SongViewModel(MusicServiceConnection musicServiceConnection) {
        super();

        playbackState = musicServiceConnection.get_playbackState();

        updateCurrentPlayerPosition();
    }

    private void updateCurrentPlayerPosition() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Long pos = PlaybackStateCompatExt.currentPlaybackPosition(playbackState.getValue());
                    if (curPlayerPosition().getValue() != pos) {
                        _curPlayerPosition.postValue(pos);
                        _curSongDuration.postValue(MusicService.getCurSongDuration());
                    }
                    try {
                        Thread.sleep(UPDATE_PLAYER_POSITION_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
