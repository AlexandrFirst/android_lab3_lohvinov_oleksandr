package oleksandr.lohvinov.lab3.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import oleksandr.lohvinov.lab3.other.Constants;

public class MusicDatabase {

    private FirebaseFirestore firestore;
    private CollectionReference songCollection;

    public Task<QuerySnapshot> getAllSongs() {
        return songCollection.get();
    }

    public MusicDatabase() {
        firestore = FirebaseFirestore.getInstance();
        songCollection = firestore.collection(Constants.SONG_COLLECTION);
    }
}
