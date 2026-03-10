package states;

public interface PlayerState {
    void play(Player player);
    void pause(Player player);
    void stop(Player player);
}
