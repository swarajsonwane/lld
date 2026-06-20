import enums.Genre;
import enums.SubscriptionPlan;
import models.*;
import streaming.BasicStreamingStrategy;
import streaming.PremiumStreamingStrategy;
import streaming.StandardStreamingStrategy;

import java.util.List;

public class NetflixLLDDemo {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("Netflix LLD System - Comprehensive Test");
        System.out.println("=".repeat(80));

        // Initialize Netflix System (Singleton)
        NetflixSystem netflix = NetflixSystem.getInstance();

        // ==================== 1. CREATE USERS ====================
        System.out.println("\n[1] Creating Users with Different Subscription Plans...");
        System.out.println("-".repeat(80));

        User user1 = new User.Builder()
                .setId("U001")
                .setName("John Doe")
                .setEmail("john@netflix.com")
                .setSubscriptionPlan(SubscriptionPlan.BASIC)
                .setStreamingStrategy(new BasicStreamingStrategy())
                .build();

        User user2 = new User.Builder()
                .setId("U002")
                .setName("Jane Smith")
                .setEmail("jane@netflix.com")
                .setSubscriptionPlan(SubscriptionPlan.STANDARD)
                .setStreamingStrategy(new StandardStreamingStrategy())
                .build();

        User user3 = new User.Builder()
                .setId("U003")
                .setName("Alex Johnson")
                .setEmail("alex@netflix.com")
                .setSubscriptionPlan(SubscriptionPlan.PREMIUM)
                .setStreamingStrategy(new PremiumStreamingStrategy())
                .build();

        netflix.registerUser(user1);
        netflix.registerUser(user2);
        netflix.registerUser(user3);
        System.out.println("✓ Created 3 users with different subscription plans");

        // ==================== 2. ADD MOVIES ====================
        System.out.println("\n[2] Adding Movies to Catalog...");
        System.out.println("-".repeat(80));

        Movie movie1 = netflix.addMovie("MOV001", "Inception", Genre.SCI_FI, 148, 2010);
        System.out.println("✓ Added: " + movie1.getTitle() + " (" + movie1.getGenre() + ")");

        Movie movie2 = netflix.addMovie("MOV002", "The Dark Knight", Genre.ACTION, 152, 2008);
        System.out.println("✓ Added: " + movie2.getTitle() + " (" + movie2.getGenre() + ")");

        Movie movie3 = netflix.addMovie("MOV003", "The Pursuit of Happyness", Genre.DRAMA, 117, 2006);
        System.out.println("✓ Added: " + movie3.getTitle() + " (" + movie3.getGenre() + ")");

        Movie movie4 = netflix.addMovie("MOV004", "Forrest Gump", Genre.DRAMA, 142, 1994);
        System.out.println("✓ Added: " + movie4.getTitle() + " (" + movie4.getGenre() + ")");

        Movie movie5 = netflix.addMovie("MOV005", "The Conjuring", Genre.HORROR, 112, 2013);
        System.out.println("✓ Added: " + movie5.getTitle() + " (" + movie5.getGenre() + ")");

        // ==================== 3. ADD SERIES ====================
        System.out.println("\n[3] Adding TV Series with Episodes...");
        System.out.println("-".repeat(80));

        Series series1 = new Series("SER001", "Breaking Bad", Genre.DRAMA);
        Episode ep1 = netflix.createEpisode("Pilot", Genre.DRAMA, 58, 1, 1);
        Episode ep2 = netflix.createEpisode("Cat's in the Bag", Genre.DRAMA, 57, 2, 1);
        Episode ep3 = netflix.createEpisode("And the Bag's in the River", Genre.DRAMA, 45, 3, 1);
        series1.addEpisode(ep1);
        series1.addEpisode(ep2);
        series1.addEpisode(ep3);
        netflix.addSeriesDirectly(series1);
        System.out.println("✓ Added: " + series1.getTitle() + " with " + series1.getWatchableItems().size() + " episodes");

        Series series2 = new Series("SER002", "Stranger Things", Genre.SCI_FI);
        Episode ep4 = netflix.createEpisode("Chapter One: The Vanishing of Will Byers", Genre.SCI_FI, 47, 1, 1);
        Episode ep5 = netflix.createEpisode("Chapter Two: The Weirdo on Maple Street", Genre.SCI_FI, 50, 2, 1);
        series2.addEpisode(ep4);
        series2.addEpisode(ep5);
        netflix.addSeriesDirectly(series2);
        System.out.println("✓ Added: " + series2.getTitle() + " with " + series2.getWatchableItems().size() + " episodes");

        // ==================== 4. SEARCH FUNCTIONALITY ====================
        System.out.println("\n[4] Testing Search Functionality...");
        System.out.println("-".repeat(80));

        // Search by title
        List<Content> searchResults = netflix.search("Dark");
        System.out.println("Search for 'Dark': Found " + searchResults.size() + " result(s)");
        for (Content content : searchResults) {
            System.out.println("  → " + content.getTitle());
        }

        // Browse by genre
        List<Content> dramaContent = netflix.browseByGenre(Genre.DRAMA);
        System.out.println("\nBrowsing Genre 'DRAMA': Found " + dramaContent.size() + " result(s)");
        for (Content content : dramaContent) {
            System.out.println("  → " + content.getTitle() + " (" + content.getDurationMinutes() + " min)");
        }

        // ==================== 5. WATCHLIST MANAGEMENT ====================
        System.out.println("\n[5] Managing Watchlist...");
        System.out.println("-".repeat(80));

        user1.addToWatchlist(movie1);
        user1.addToWatchlist(series1);
        user1.addToWatchlist(movie2);

        System.out.println("User '" + user1.getName() + "' Watchlist (" + user1.getWatchlist().size() + " items):");
        for (Content content : user1.getWatchlist()) {
            System.out.println("  → " + content.getTitle());
        }

        // ==================== 6. PLAYING CONTENT ====================
        System.out.println("\n[6] Playing Content with Player States...");
        System.out.println("-".repeat(80));

        System.out.println("\nUser: " + user2.getName() + " | Subscription: " + user2.getSubscriptionPlan());
        netflix.playContent(movie1, user2);
        netflix.getPlayer().clickPlay();
        netflix.getPlayer().clickPause();
        netflix.getPlayer().clickPlay();
        netflix.getPlayer().clickStop();

        System.out.println("\nUser: " + user3.getName() + " | Subscription: " + user3.getSubscriptionPlan());
        netflix.playContent(series2, user3);
        netflix.getPlayer().clickPlay();
        netflix.getPlayer().clickNext();
        netflix.getPlayer().clickStop();

        // ==================== 7. WATCH PROGRESS ====================
        System.out.println("\n[7] Tracking Watch Progress...");
        System.out.println("-".repeat(80));

        user2.recordWatchProgress(movie1, 75);
        user3.recordWatchProgress(series2, 120);

        System.out.println("Watch History for " + user2.getName() + ":");
        for (Content content : user2.getWatchHistory()) {
            int progress = user2.getWatchProgress(content);
            System.out.println("  → " + content.getTitle() + " - " + progress + " minutes watched");
        }

        System.out.println("\nWatch History for " + user3.getName() + ":");
        for (Content content : user3.getWatchHistory()) {
            int progress = user3.getWatchProgress(content);
            System.out.println("  → " + content.getTitle() + " - " + progress + " minutes watched");
        }

        // ==================== 8. CATALOG OVERVIEW ====================
        System.out.println("\n[8] Complete Netflix Catalog...");
        System.out.println("-".repeat(80));

        List<Content> allContent = netflix.getCatalog();
        System.out.println("Total content available: " + allContent.size());
        System.out.println("\nCatalog Summary:");
        int movieCount = (int) allContent.stream().filter(c -> c instanceof Movie).count();
        int seriesCount = (int) allContent.stream().filter(c -> c instanceof Series).count();
        System.out.println("  - Movies: " + movieCount);
        System.out.println("  - TV Series: " + seriesCount);

        int totalMinutes = allContent.stream().mapToInt(Content::getDurationMinutes).sum();
        System.out.println("  - Total content duration: " + totalMinutes + " minutes");

        // ==================== 9. USER STATISTICS ====================
        System.out.println("\n[9] User Statistics...");
        System.out.println("-".repeat(80));

        User[] users = {user1, user2, user3};
        for (User user : users) {
            System.out.println("\n" + user.getName() + " (" + user.getEmail() + ")");
            System.out.println("  Subscription Plan: " + user.getSubscriptionPlan());
            System.out.println("  Watchlist Items: " + user.getWatchlist().size());
            System.out.println("  Watch History: " + user.getWatchHistory().size());
        }

        // ==================== TEST COMPLETION ====================
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✓ Netflix LLD System Test Completed Successfully!");
        System.out.println("=".repeat(80));
    }
}

