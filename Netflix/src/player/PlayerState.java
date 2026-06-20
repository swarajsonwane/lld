package player;

/**
 * The PlayerState interface defines the behavior of different states of a media player.
 * Each state (e.g., Playing, Paused, Stopped) will implement this interface to provide
 * specific functionality for that state.
 */
public interface PlayerState {

    void play(VideoPlayer player);
    void pause(VideoPlayer player);
    void stop(VideoPlayer player);
    void next(VideoPlayer player);
}
