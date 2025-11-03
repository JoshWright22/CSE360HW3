package application;

import java.util.ArrayList;
import java.util.List;

class Review {
    private int id;
    private String content;
    private int previousVersionId = 0;

    public Review(int id, String content) { this.id = id; this.content = content; }
    public Review(int id, String content, int previousVersionId) { this.id = id; this.content = content; this.previousVersionId = previousVersionId; }
    public int getId() { return id; }
    public int getPreviousVersionId() { return previousVersionId; }
}