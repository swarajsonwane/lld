package observer;

import models.Content;

import java.util.ArrayList;
import java.util.List;

public class ContentSubject {

    private  final List<ContentObserver> observers = new ArrayList<>();

    public void subscribe(ContentObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(ContentObserver observer) {
        observers.remove(observer);
    }

    public void notifyNewContent(Content content) {
        for (ContentObserver observer : observers) {
            observer.onNewContentAvailable(content);
        }
    }
}
