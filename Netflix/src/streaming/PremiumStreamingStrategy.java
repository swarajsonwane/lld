package streaming;

import models.Content;

public class PremiumStreamingStrategy implements StreamingStrategy {
    @Override
    public void stream(Content content, int fromMinute) {
        System.out.println("Streaming " + content.getTitle() + " in 4K quality from minute " + fromMinute);
    }

    @Override
    public String getResolution() {
        return "4k";
    }
}
