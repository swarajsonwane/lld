package models;

import enums.SubscriptionPlan;
import observer.ContentObserver;
import streaming.StreamingStrategy;

import java.util.*;

public class User implements ContentObserver {
    private final String id;
    private final String name;
    private final String email;
    private final SubscriptionPlan subscriptionPlan;
    private final List<Content> watchHistory = new ArrayList<>();
    private final List<Content> watchlist = new ArrayList<>();
    private final Map<String, Integer> watchProgress = new HashMap<>(); // Key: Content ID, Value: Minutes watched
    private final StreamingStrategy streamingStrategy;

    private User(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.subscriptionPlan = builder.subscriptionPlan;
        this.streamingStrategy = builder.streamingStrategy;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onNewContentAvailable(Content content) {
        System.out.println("New on Netflix: " + content.getTitle() + " - Genre: " + content.getGenre());
    }

    public String getEmail() {
        return email;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public List<Content> getWatchHistory() {
        return Collections.unmodifiableList(watchHistory);
    }

    public List<Content> getWatchlist() {
        return Collections.unmodifiableList(watchlist);
    }

    public StreamingStrategy getStreamingStrategy() {
        return streamingStrategy;
    }

    public int getWatchProgress(Content content) {
        return watchProgress.getOrDefault(content.getId(), 0);
    }

    // My list

    public void addToWatchlist(Content content) {
        if (!watchlist.contains(content)) {
            watchlist.add(content);
        }
    }

    public void removeFromWatchlist(Content content) {
        watchlist.remove(content);
    }

    public void recordWatchProgress(Content contentToPlay, int minutesWatched) {
        if(!watchHistory.contains(contentToPlay)) {
            watchHistory.add(contentToPlay);
        }
        watchProgress.put(contentToPlay.getId(), minutesWatched);
    }


    //Builder
    public static class Builder {
        private String id;
        private String name;
        private String email;
        private SubscriptionPlan subscriptionPlan;
        private StreamingStrategy streamingStrategy;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
            this.subscriptionPlan = subscriptionPlan;
            return this;
        }

        public Builder setStreamingStrategy(StreamingStrategy streamingStrategy) {
            this.streamingStrategy = streamingStrategy;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
