package streaming;

import models.Content;

public class StandardStreamingStrategy implements StreamingStrategy {
    @Override
    public void stream(Content content, int fromMinute) {
        System.out.println("Streaming " + content.getTitle() + " in HD quality from minute " + fromMinute);
    }

    @Override
    public String getResolution() {
        return "1080p";
    }
}
