package models;

import enums.Genre;

import java.util.List;

/**
 * Represents a movie in the Netflix application.
 * Leaf node in the Composite pattern.
 * It's a single watchable item and does not contain any other content.
 */

public class Movie implements  Content {
    private String id;
    private String title;
    private Genre genre;
    private int durationMinutes;
    private final int releaseYear;

    public Movie(String id, String title, Genre genre, int durationMinutes, int releaseYear) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.releaseYear = releaseYear;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Genre getGenre() {
        return genre;
    }

    @Override
    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public List<Content> getWatchableItems() {
        return List.of(this); // A movie is a single watchable item
    }


}
