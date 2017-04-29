package hip613.hackhealth2017_48.models;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    String id;
    String title;
    String category;
    String photoURL;
    String description;
    int upvotes;
    Date createdAt;
    ArrayList<String> comments;

    //this constuctor is used when creating new posts
    public Post(String title, String category, String photoURL, String description, Date createdAt, ArrayList<String> comments) {
       this("", title, category, photoURL, description, 0, createdAt, comments);
    }

    //this constructor is used when fetching posts from the db
    public Post(String id, String title, String category, String photoURL, String description, int upvotes, Date createdAt, ArrayList<String> comments) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.photoURL = photoURL;
        this.description = description;
        this.upvotes = upvotes;
        this.createdAt = createdAt;
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public String getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }
}
