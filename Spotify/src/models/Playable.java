package models;

import java.util.List;

public interface Playable {
    List<Song> getTracks();
}

//This is composite pattern.. where song album and playlist are composites or leaf nodes
