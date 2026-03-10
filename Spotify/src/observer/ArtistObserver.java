package observer;

import models.Album;
import models.Artist;

public interface ArtistObserver {
    void update(Artist artist, Album newAlbum);
}
