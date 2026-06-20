package enums;

public enum SubscriptionPlan {
    BASIC(1, "480p"),
    STANDARD(2, "1080p"),
    PREMIUM(4, "4K");

    private final int maxScreens;
    private final String maxResolution;

    SubscriptionPlan(int maxScreens, String maxResolution) {
        this.maxScreens = maxScreens;
        this.maxResolution = maxResolution;
    }
}
