package search;

import enums.Genre;
import models.Content;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SearchService {
    public List<Content> searchByTitle(List<Content> catalog, String title) {
        return catalog.stream()
                .filter(content -> content.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    public List<Content> searchByGenre(List<Content> catalog, Genre   genre) {
        return catalog.stream()
                .filter(content -> content.getGenre() == genre)
                .toList();
    }
}
