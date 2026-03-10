package recommendations;

import models.Song;

import java.util.List;

public interface RecommendationStrategy {
    List<Song> recommend(List<Song> allSongs);
}
