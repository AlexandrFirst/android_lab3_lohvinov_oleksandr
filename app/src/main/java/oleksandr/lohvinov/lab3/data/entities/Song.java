package oleksandr.lohvinov.lab3.data.entities;

import java.util.Objects;

public class Song {

    public Song(String mediaId, String title, String subtitle, String songUrl, String imageUrl) {
        this.mediaId = mediaId;
        this.title = title;
        this.subtitle = subtitle;
        this.songUrl = songUrl;
        this.imageUrl = imageUrl;
    }

    public Song() {
    }

    public String mediaId = "";
    public String title = "";
    public String subtitle = "";
    public String songUrl = "";
    public String imageUrl = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(mediaId, song.mediaId) &&
                Objects.equals(title, song.title) &&
                Objects.equals(subtitle, song.subtitle) &&
                Objects.equals(songUrl, song.songUrl) &&
                Objects.equals(imageUrl, song.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaId, title, subtitle, songUrl, imageUrl);
    }
}
