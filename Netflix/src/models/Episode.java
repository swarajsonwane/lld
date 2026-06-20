package models;

import enums.Genre;

import java.util.List;

public class Episode implements Content {
    private String id;
    private String title;
    private Genre genre;
    private int durationMinutes;
    private int episodeNumber;
    private int seasonNumber;

    public Episode(String id, String title, Genre genre, int durationMinutes, int episodeNumber, int seasonNumber) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
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
        return List.of(this); // An episode is a single watchable item
    }

        @Override
    public String toString() {
            return "Episode{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", genre=" + genre +
                    ", durationMinutes=" + durationMinutes +
                    ", episodeNumber=" + episodeNumber +
                    ", seasonNumber=" + seasonNumber +
                    '}';
        }
}
