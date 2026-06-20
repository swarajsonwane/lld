package models;

import enums.Genre;

import java.util.ArrayList;
import java.util.List;

public class Series implements Content {
    private String id;
    private String title;
    private Genre genre;
    private List<Content> episodes = new ArrayList<>(); // List of episodes

    public Series(String id, String title, Genre genre) {
        this.id = id;
        this.title = title;
        this.genre = genre;
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
        return episodes.stream().mapToInt(Content::getDurationMinutes).sum(); // Total duration of all episodes
    }

    @Override
    public List<Content> getWatchableItems() {
        return new ArrayList<>(episodes); // Return a copy of the episodes list
    }

    @Override
    public String toString() {
        return "Series{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", genre=" + genre +
                ", episodes=" + episodes +
                '}';
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }
}
