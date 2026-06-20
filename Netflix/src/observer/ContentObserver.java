package observer;

import models.Content;

public interface ContentObserver {
    void onNewContentAvailable(Content content);
}
