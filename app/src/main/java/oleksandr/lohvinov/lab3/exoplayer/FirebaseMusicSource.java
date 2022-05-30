package oleksandr.lohvinov.lab3.exoplayer;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;

import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import oleksandr.lohvinov.lab3.data.entities.Song;
import oleksandr.lohvinov.lab3.data.remote.MusicDatabase;

public class FirebaseMusicSource {


    public List<MediaMetadataCompat> songs = new ArrayList();
    private MusicDatabase musicDatabase;

    @Inject
    public FirebaseMusicSource(MusicDatabase musicDatabase) {
        this.musicDatabase = musicDatabase;
    }

    public void fetchMediaData() {
        setState(MusicSourceState.STATE_INITIALIZING);
        List<Song> allSongs = musicDatabase.getAllLocalSongs();


        songs = Stream.of(allSongs).flatMap(songs -> songs.stream()).map(song -> new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.subtitle)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.songUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle).build())
                .collect(Collectors.toList());

        setState(MusicSourceState.STATE_INITIALIZED);
    }


    public ConcatenatingMediaSource asMediaSource(DefaultDataSourceFactory dataSourceFactory) {
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        songs.forEach(song -> {
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)));
            concatenatingMediaSource.addMediaSource(mediaSource);
        });
        return concatenatingMediaSource;
    }

    public List<MediaBrowserCompat.MediaItem> asMediaItems() {
        return songs.stream().map(song -> {
            MediaDescriptionCompat desc = new MediaDescriptionCompat.Builder()
                    .setMediaUri(Uri.parse(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)))
                    .setTitle(song.getDescription().getTitle())
                    .setSubtitle(song.getDescription().getSubtitle())
                    .setMediaId(song.getDescription().getMediaId())
                    .setIconUri(song.getDescription().getIconUri())
                    .build();
            return new MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE);
        }).collect(Collectors.toList());
    }

    private List<ListenerItem> onReadyListeners = new ArrayList<>();

    private MusicSourceState state = MusicSourceState.STATE_CREATED;

    public void setState(MusicSourceState state) {
        if (state == MusicSourceState.STATE_INITIALIZED || state == MusicSourceState.STATE_ERROR) {
            synchronized (onReadyListeners) {
                this.state = state;
                onReadyListeners.forEach(listener -> {
                    listener.Func(state == MusicSourceState.STATE_INITIALIZED);
                });
            }
        } else {
            this.state = state;
        }
    }

    public boolean whenReady(ListenerItem action) {
        if (state == MusicSourceState.STATE_CREATED || state == MusicSourceState.STATE_INITIALIZING) {
            onReadyListeners.add(action);
            return false;
        } else {
            action.Func(state == MusicSourceState.STATE_INITIALIZED);
            return true;
        }
    }
}

