package player;

import enums.PlayerStatus;

public class PausedState implements  PlayerState {
    @Override
    public void play(VideoPlayer player) {
        System.out.println("Resuming: " + player.getCurrentContent().getTitle());
        player.changeState(new PlayingState());
        player.setStatus(PlayerStatus.PLAYING);
        player.playCurrentContent();// resume from where it was paused
    }

    @Override
    public void pause(VideoPlayer player) {
        System.out.println("Already paused: " + player.getCurrentContent().getTitle());
    }

    @Override
    public void stop(VideoPlayer player) {
        System.out.println("Stopping from paused state: " + player.getCurrentContent().getTitle());
        player.stop();
    }

    @Override
    public void next(VideoPlayer player) {
        System.out.println("Skipping to next content from paused state.");
        player.changeState(new PlayingState());
        player.setStatus(PlayerStatus.PLAYING);
        player.advanceToNextContent();
    }

}
