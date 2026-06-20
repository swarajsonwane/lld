package factory;

import enums.Genre;
import models.Episode;
import models.Movie;
import models.Series;

/**
 * Factory class for creating content objects.
 * This class can be extended to create different types of content (e.g., movies, TV shows) based on input parameters.
 */
public class ContentFactory {
    private int movieCounter = 0;
    private int seriesCounter = 0;
    private int episodeCounter = 0;

    public Movie createMovie(String title, Genre genre, int durationMinutes, int releaseYear) {
        movieCounter++;
        String id = "M" + movieCounter; // Generate a unique ID for the movie
        return new Movie(id, title, genre, durationMinutes, releaseYear);
    }

    public Series createSeries(String title, Genre genre) {
        seriesCounter++;
        String id = "S" + seriesCounter; // Generate a unique ID for the series
        return new Series(id, title, genre);
    }

    public Episode createEpisode(String title, Genre genre, int durationMinutes, int episodeNumber, int seasonNumber) {
        episodeCounter++;
        String id = "E" + episodeCounter; // Generate a unique ID for the episode
        return new Episode(id, title, genre, durationMinutes, episodeNumber, seasonNumber);
    }
}
