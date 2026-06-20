package models;

import enums.Genre;

import java.util.List;

/**
 * Composite Patterns - Component Interface
 * Both Movie (leaf) and Series implements this interface. This allows us to treat both Movie and Series uniformly when dealing with content in the application.
 */
public interface Content {
    String getId();
    String getTitle();
    Genre getGenre();
    int getDurationMinutes(); // For Series, this can return the total duration of all episodes
    List<Content> getWatchableItems(); // For Series, this can return a list of episodes; for Movie, it can return an list with one element
}
