package player;

import enums.PlayerStatus;
import models.Content;
import models.User;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayer {
    private PlayerState state;
    private PlayerStatus status;
    private List<Content> queue = new ArrayList<>();
    private  Content currentContent;
    private int currentIndex = -1;
    private User currentUser;
    private int currentMinute = 0;

    public VideoPlayer() {
        this.state = new StoppedState();
        this.status = PlayerStatus.STOPPED;
    }

    public void load(Content content, User user) {
        this.currentContent = content;
        this.currentUser = user;
        this.currentIndex = 0;
        this.currentMinute = 0;
        this.queue.clear();
        this.queue.addAll(content.getWatchableItems());
        System.out.println("Loaded content: " + content.getTitle() + " for user: " + user.getName());
        this.state = new StoppedState();
        this.status = PlayerStatus.STOPPED;
    }

    //Actions delegated to state
    public void clickPlay(){ state.play(this);}
    public void clickPause(){ state.pause(this);}
    public void clickStop(){ state.stop(this);}
    public void clickNext(){ state.next(this);}

    //How videoPlayer interacts with the state
    public void changeState(PlayerState state) {
        this.state = state;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public PlayerStatus getStatus() {
        return status;
    }
    public Content getCurrentContent() {
        return currentContent;
    }
    public boolean hasQueue() {
        return !queue.isEmpty();
    }

    public void playCurrentContent() {
      if(currentIndex >= 0 && currentIndex < queue.size()) {
          Content contentToPlay = queue.get(currentIndex);
         int resumeFrom = currentUser.getWatchProgress(contentToPlay);
         currentUser.getStreamingStrategy().stream(contentToPlay, resumeFrom);
         currentUser.recordWatchProgress(contentToPlay, resumeFrom+1); // Simulate watching 1 minute
      }
    }

    public boolean hasNextContent() {
        return currentIndex + 1 < queue.size();
    }

    public void advanceToNextContent() {
        if(hasNextContent()) {
            currentIndex++;
            currentMinute = 0;
            playCurrentContent();
        }else{
            System.out.println("No more content in the queue.");
            stop();
        }
    }

    public  void stop() {
        this.state = new StoppedState();
        this.status = PlayerStatus.STOPPED;
        this.currentContent = null;
        System.out.println("Playback stopped.");
    }
}
