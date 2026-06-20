package streaming;

import models.Content;

public class BasicStreamingStrategy implements StreamingStrategy {
    @Override
    public void stream(Content content, int fromMinute) {
        System.out.println("Streaming " + content.getTitle() + " in SD quality from minute " + fromMinute);
    }

    @Override
    public String getResolution() {
        return "480p";
    }
}
