package player;

import enums.PlayerStatus;

public class PlayingState implements PlayerState{
    @Override
    public void play(VideoPlayer player) {
        System.out.println("Already playing: " + player.getCurrentContent().getTitle());
    }

    @Override
    public void pause(VideoPlayer player) {
        System.out.println("Pausing: " + player.getCurrentContent().getTitle());
        player.changeState(new PausedState());
        player.setStatus(PlayerStatus.PAUSED);
    }

    @Override
    public void stop(VideoPlayer player) {
        System.out.println("Stopping: " + player.getCurrentContent().getTitle());
        player.stop();
    }

    @Override
    public void next(VideoPlayer player) {
        System.out.println("Skipping to next content.");
        player.changeState(new PlayingState());
        player.setStatus(PlayerStatus.PLAYING);
        player.advanceToNextContent();
    }
}
