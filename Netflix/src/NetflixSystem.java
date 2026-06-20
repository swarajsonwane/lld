import enums.Genre;
import factory.ContentFactory;
import models.*;
import observer.ContentSubject;
import player.VideoPlayer;
import search.SearchService;

import java.util.*;

public class NetflixSystem {
    private static volatile NetflixSystem instance;

    public static NetflixSystem getInstance() {
        if (instance == null) {
            synchronized (NetflixSystem.class) {
                if (instance == null) {
                    instance = new NetflixSystem();
                }
            }
        }
        return instance;
    }

    private final Map<String, Content> catalog = new LinkedHashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final VideoPlayer player;
    private final SearchService searchService;
    private final ContentSubject contentSubject;
    private final ContentFactory contentFactory;

    private NetflixSystem() {
        this.player = new VideoPlayer();
        this.searchService = new SearchService();
        this.contentSubject = new ContentSubject();
        this.contentFactory = new ContentFactory();
    }

    //Content Management
    public Movie addMovie(String id, String title, Genre genre, int durationMinutes, int releaseYear) {
        Movie movie = contentFactory.createMovie( title, genre, durationMinutes, releaseYear);
        catalog.put(id, movie);
        contentSubject.notifyNewContent(movie);
        return movie;
    }

    public Series addSeries(String id, String title, Genre genre, List<Episode> episodes) {
        Series series = contentFactory.createSeries( title, genre);
        episodes.forEach(series::addEpisode);
        catalog.put(id, series);
        contentSubject.notifyNewContent(series);
        return series;
    }

    public void addSeriesDirectly(Series series) {
        catalog.put(series.getId(), series);
        contentSubject.notifyNewContent(series);
    }

    public Episode createEpisode(String title, Genre genre, int durationMinutes, int episodeNumber, int seasonNumber) {
        return contentFactory.createEpisode( title, genre, durationMinutes, episodeNumber, seasonNumber);
    }

    //User Management
    public void registerUser(User user) {
        users.put(user.getId(), user);
        contentSubject.subscribe(user);
    }

    //Playback
    public VideoPlayer getPlayer() {
        return player;
    }

    public void playContent(Content content, User user) {
       player.load(content, user);
       player.clickPlay();
    }

    //Search
    public List<Content> search(String query) {
        return searchService.searchByTitle(new ArrayList<>(catalog.values()), query);
    }

    public List<Content> browseByGenre(Genre genre) {
        return searchService.searchByGenre(new ArrayList<>(catalog.values()), genre);
    }

    public List<Content> getCatalog() {
        return new ArrayList<>(catalog.values());
    }
}
