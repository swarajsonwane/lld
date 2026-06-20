package streaming;

import enums.SubscriptionPlan;
import models.Content;

public interface StreamingStrategy {
    void stream(Content content,int fromMinute);
    String getResolution();

    static StreamingStrategy forPlan(SubscriptionPlan plan) {

        return switch (plan) {
            case BASIC -> new BasicStreamingStrategy();
            case STANDARD -> new StandardStreamingStrategy();
            case PREMIUM -> new PremiumStreamingStrategy();
        };

    }
}
