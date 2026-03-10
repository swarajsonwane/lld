package states;

import enums.PlayerStatus;
import models.Playable;
import models.Song;
import models.User;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private PlayerState state;
    private PlayerStatus status;
    private List<Song> queue = new ArrayList<>();
    private int currentIndex = -1;
    private Song currentSong;
    private User currentUser;

    public Player() {
        this.state = new StoppedState();
        this.status = PlayerStatus.STOPPED;
    }

    public void load(Playable playable, User user) {
        this.currentUser = user;
        this.queue = playable.getTracks();
        this.currentIndex = 0;
        System.out.printf("Loaded %d tracks for user %s.%n", queue.size(), user.getName());
        this.state = new StoppedState();
    }

    public void playCurrentSongInQueue() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            Song songToPlay = queue.get(currentIndex);
            currentUser.getPlaybackStrategy().play(songToPlay, this);
        }
    }

    // Methods for state transitions
    //First is stopped..once clicked play it goes in stopped state and check if condition to make state transition to play state
    public void clickPlay() { state.play(this); }
    public void clickPause() { state.pause(this); }

    public void clickNext() {
        if (currentIndex < queue.size() - 1) {
            currentIndex++;
            playCurrentSongInQueue();
        } else {
            System.out.println("End of queue.");
            state.stop(this);
        }
    }

    // Getters and Setters used by States
    public void changeState(PlayerState state) { this.state = state; }
    public void setStatus(PlayerStatus status) { this.status = status; }
    public void setCurrentSong(Song song) { this.currentSong = song; }
    public boolean hasQueue() { return !queue.isEmpty(); }
}
