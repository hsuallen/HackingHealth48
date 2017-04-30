package hip613.hackhealth2017_48.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
    public Post(String title, String category, String photoURL, String description) {
       this("", title, category, photoURL, description, 0, null, new ArrayList<String>());
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

    //makes a JSON of the object
    public JSONObject toJSON(){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");



        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("title", getTitle());
            jsonObject.put("category", getCategory());
            jsonObject.put("photo", getPhotoURL());
            jsonObject.put("description", getDescription());
            jsonObject.put("upvotes", getUpvotes());
            jsonObject.put("comments", getComments());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

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
