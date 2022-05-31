package oleksandr.lohvinov.lab3.ui.viewmodels;

import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE;
import static oleksandr.lohvinov.lab3.other.Constants.MEDIA_ROOT_ID;
import static oleksandr.lohvinov.lab3.other.Constants.RANDOM_SONG_LIST;
import static oleksandr.lohvinov.lab3.other.Constants.REPEAT_ALL_SONG;
import static oleksandr.lohvinov.lab3.other.Constants.REPEAT_ONE_SONG;
import static oleksandr.lohvinov.lab3.other.Constants.STRAIGHT_SONG_LIST;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import oleksandr.lohvinov.lab3.data.entities.Song;
import oleksandr.lohvinov.lab3.exoplayer.MusicServiceConnection;
import oleksandr.lohvinov.lab3.exoplayer.PlaybackStateCompatExt;
import oleksandr.lohvinov.lab3.other.Event;
import oleksandr.lohvinov.lab3.other.Resource;
import oleksandr.lohvinov.lab3.other.Status;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private MusicServiceConnection musicServiceConnection;

    private MutableLiveData<Resource<List<Song>>> _mediaItems = new MutableLiveData<>();
    private LiveData<Resource<List<Song>>> mediaItems = _mediaItems;

    public MutableLiveData<Event<Resource<Boolean>>> isConnected;
    public MutableLiveData<Event<Resource<Boolean>>> networkError;
    public MutableLiveData<MediaMetadataCompat> curPlayingSong;
    public MutableLiveData<PlaybackStateCompat> playbackState;
    public MutableLiveData<String> songOrderType;

    @Inject
    public MainViewModel(MusicServiceConnection musicServiceConnection) {
        this.musicServiceConnection = musicServiceConnection;

        isConnected = musicServiceConnection._isConnected;
        networkError = musicServiceConnection._networkError;
        curPlayingSong = musicServiceConnection._curPlayingSong;
        playbackState = musicServiceConnection._playbackState;
        songOrderType = new MutableLiveData<>();

        _mediaItems.postValue(Resource.loading(null));

        musicServiceConnection.subscribe(MEDIA_ROOT_ID, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                super.onChildrenLoaded(parentId, children);
                List items = children.stream().map(it -> new Song(it.getMediaId(),
                        it.getDescription().getTitle().toString(),
                        it.getDescription().getSubtitle().toString(),
                        it.getDescription().getMediaUri().toString(),
                        it.getDescription().getIconUri().toString())).collect(Collectors.toList());
                _mediaItems.postValue(Resource.success(items));
            }
        });
    }

    public void skipToRandomSong() {
        Resource<List<Song>> mediaItems = getMediaItems().getValue();
        Status s = mediaItems.getStatus();
        switch (s) {
            case SUCCESS:
                List<Song> songs = mediaItems.getData();
                int randomIndex = ThreadLocalRandom.current().nextInt(0, songs.size());
                Song song = songs.get(randomIndex);
                playOrToggleSong(song);
                break;
            case ERROR:
            case LOADING:
                return;
        }
    }

    public void SetMode(String mode) {
        if (musicServiceConnection.transportControl() == null) {
            return;
        }
        if (mode.isEmpty() || mode.equals(STRAIGHT_SONG_LIST) || mode.equals(RANDOM_SONG_LIST)) {
            musicServiceConnection.transportControl().setRepeatMode(REPEAT_MODE_NONE);
        } else if (mode.equals(REPEAT_ONE_SONG)) {
            musicServiceConnection.transportControl().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
        } else if (mode.equals(REPEAT_ALL_SONG)) {
            musicServiceConnection.transportControl().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
        } else {
            musicServiceConnection.transportControl().setRepeatMode(REPEAT_MODE_NONE);
        }

    }

    public void skipToNextSong() {
        musicServiceConnection.transportControl().skipToNext();
    }

    public void skipToPreviousSong() {
        musicServiceConnection.transportControl().skipToPrevious();
    }

    public void seekTo(Long pos) {
        musicServiceConnection.transportControl().seekTo(pos);
    }

    public void playOrToggleSong(Song mediaItem) {
        playOrToggleSong(mediaItem, false);
    }

    public void playOrToggleSong(Song mediaItem, boolean toggle) {
        boolean isPrepared = PlaybackStateCompatExt.isPrepared(playbackState.getValue());
        if (isPrepared && mediaItem.mediaId ==
                curPlayingSong.getValue().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) {
            if (PlaybackStateCompatExt.isPlaying(playbackState.getValue())) {
                if (toggle) {
                    musicServiceConnection.transportControl().pause();
                }
            } else if (PlaybackStateCompatExt.isPlayEnabled(playbackState.getValue())) {
                musicServiceConnection.transportControl().play();
            }
        } else {
            musicServiceConnection.transportControl().playFromMediaId(mediaItem.mediaId, null);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, new MediaBrowserCompat.SubscriptionCallback() {
        });
    }

    public LiveData<Resource<List<Song>>> getMediaItems() {
        return _mediaItems;
    }
}
