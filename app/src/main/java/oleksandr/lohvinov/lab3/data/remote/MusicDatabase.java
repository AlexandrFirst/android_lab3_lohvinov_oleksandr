package oleksandr.lohvinov.lab3.data.remote;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import oleksandr.lohvinov.lab3.data.entities.Song;
import oleksandr.lohvinov.lab3.other.Constants;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MusicDatabase {

    private FirebaseFirestore firestore;
    private CollectionReference songCollection;
    private Context context;

    public Task<QuerySnapshot> getAllSongs() {
        return songCollection.get();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public List<Song> getAllLocalSongs() {
        List<Song> allSongs = new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA
        };
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor audioCursor = context.getContentResolver().query(musicUri, null, null, null, null);

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int audioIdIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    int audioTitleIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int audioArtistIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int audioSubtitleIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    int audioDataIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

                    String songId = audioCursor.getString(audioIdIndex);
                    String songTitle = audioCursor.getString(audioTitleIndex);
                    String songArtist = audioCursor.getString(audioArtistIndex);
                    String sonSubtitle = audioCursor.getString(audioSubtitleIndex);

                    //Uri songUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
                    String songUri = audioCursor.getString(audioDataIndex);


                    Song s = new Song(songId, songTitle, songArtist, songUri.toString(), songUri.toString());
                    allSongs.add(s);

                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        return allSongs;
    }

    public MusicDatabase(Context context) {
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        songCollection = firestore.collection(Constants.SONG_COLLECTION);
    }


}
