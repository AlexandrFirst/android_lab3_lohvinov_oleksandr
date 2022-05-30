package oleksandr.lohvinov.lab3.exoplayer;

import static oleksandr.lohvinov.lab3.other.Constants.NETWORK_ERROR;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.MutableLiveData;

import oleksandr.lohvinov.lab3.other.Event;
import oleksandr.lohvinov.lab3.other.Resource;

public class MusicServiceConnection {
    private Context context;

    public MutableLiveData<Event<Resource<Boolean>>> _isConnected = new MutableLiveData<Event<Resource<Boolean>>>();

    public MutableLiveData<Event<Resource<Boolean>>> _networkError = new MutableLiveData<Event<Resource<Boolean>>>();

    public MutableLiveData<PlaybackStateCompat> _playbackState = new MutableLiveData<PlaybackStateCompat>();

    public MutableLiveData<MediaMetadataCompat> _curPlayingSong = new MutableLiveData<MediaMetadataCompat>();
    private MediaBrowserConnectionCallback mediaBrowserConnectionCallback;

    private MediaBrowserCompat mediaBrowser;

    public MusicServiceConnection(Context context) {
        this.context = context;

        mediaBrowserConnectionCallback = new MediaBrowserConnectionCallback(this.context);

        mediaBrowser = new MediaBrowserCompat(
                this.context,
                new ComponentName(this.context, MusicService.class),
                mediaBrowserConnectionCallback,
                null);


        mediaBrowser.connect();
    }






    private MediaControllerCompat mediaController;

    public MediaControllerCompat.TransportControls transportControl() {
        return mediaController.getTransportControls();
    }

    public void subscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback){
        mediaBrowser.subscribe(parentId, callback);
    }

    public void unsubscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback){
        mediaBrowser.unsubscribe(parentId, callback);
    }

    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

        private Context context;

        private MediaBrowserConnectionCallback(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onConnected() {
            mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
            mediaController.registerCallback(new MediaControllerCallback());

            _isConnected.postValue(new Event<>(Resource.success(true)));
        }

        @Override
        public void onConnectionSuspended() {
            _isConnected.postValue(new Event<>(Resource.error("The connection was suspended", false)));
        }

        @Override
        public void onConnectionFailed() {
            _isConnected.postValue(new Event<>(Resource.error("Couldn't connect to media browser", false)));
        }
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            _playbackState.postValue(state);

        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            _curPlayingSong.postValue(metadata);
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);

            switch (event) {
                case NETWORK_ERROR:
                    _networkError.postValue(new Event<>(
                            Resource.error(
                                    "Couldn't connect to the server. Please check your internet connection.",
                                    null)));
                    break;
            }
        }

        @Override
        public void onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended();
        }
    }

    public MutableLiveData<Event<Resource<Boolean>>> get_isConnected() {
        return _isConnected;
    }

    public void set_isConnected(MutableLiveData<Event<Resource<Boolean>>> _isConnected) {
        this._isConnected = _isConnected;
    }

    public MutableLiveData<Event<Resource<Boolean>>> get_networkError() {
        return _networkError;
    }

    public void set_networkError(MutableLiveData<Event<Resource<Boolean>>> _networkError) {
        this._networkError = _networkError;
    }

    public MutableLiveData<PlaybackStateCompat> get_playbackState() {
        return _playbackState;
    }

    public void set_playbackState(MutableLiveData<PlaybackStateCompat> _playbackState) {
        this._playbackState = _playbackState;
    }

    public MutableLiveData<MediaMetadataCompat> get_curPlayingSong() {
        return _curPlayingSong;
    }

    public void set_curPlayingSong(MutableLiveData<MediaMetadataCompat> _curPlayingSong) {
        this._curPlayingSong = _curPlayingSong;
    }


}
