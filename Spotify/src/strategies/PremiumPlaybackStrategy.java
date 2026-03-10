package strategies;

import models.Song;
import states.Player;

public class PremiumPlaybackStrategy implements PlaybackStrategy {


    @Override
    public void play(Song song, Player player) {
        player.setCurrentSong(song);
        System.out.printf("Premium User is now playing: %s%n", song);
    }
}

