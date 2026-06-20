package player;

public class StoppedState implements  PlayerState {
    @Override
    public void play(VideoPlayer player) {
        System.out.println("Starting playback: " + player.getCurrentContent().getTitle());
        player.changeState(new PlayingState());
        player.setStatus(enums.PlayerStatus.PLAYING);
        player.playCurrentContent();
    }

    @Override
    public void pause(VideoPlayer player) {
        System.out.println("Cannot pause. Player is stopped.");
    }

    @Override
    public void stop(VideoPlayer player) {
        System.out.println("Already stopped.");
    }

    @Override
    public void next(VideoPlayer player) {
        System.out.println("Cannot skip to next. Player is stopped.");
    }
}
