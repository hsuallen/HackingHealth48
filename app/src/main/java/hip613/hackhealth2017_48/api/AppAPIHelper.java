package hip613.hackhealth2017_48.api;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import hip613.hackhealth2017_48.R;
import hip613.hackhealth2017_48.models.Post;

public class AppAPIHelper {
    //ACTIVITY_NAME for debugging, queryURL is the URL for the API
    private final String ACTIVITY_NAME = "AppAPIHelper";
    private final String BASE = "https://aqueous-beach-97404.herokuapp.com/";
    private final String CLOUDBASE = "http://res.cloudinary.com/allenhsu/image/upload/";
    public final String NEW_POST = "api/posts/newPost";
    public final String GET_ALL = "api/posts/getAll";
    public final String UPVOTE = "api/posts/upvote";
    public final String REMOVE = "api/posts/removePost";
    public final String GET_BY_ID = "api/posts/getByID";
    public final String PNG = ".png";
    //post field names
    private final String ID = "_id";
    private final String TITLE = "title";
    private final String CATEGORY = "category";
    private final String PHOTO = "photo";
    private final String DESCRIPTION = "description";
    private final String CREATED_AT = "createdAt";
    private final String COMMENTS = "comments";
    private final String UPVOTES = "upvotes";

    private HttpURLConnection conn;

    //constructor
    public AppAPIHelper(){
    }

    //makeQuery(String foodName) takes a food name and returns the API query
    public String makeQuery(String query){
        return BASE + query;
    }

    //openConnection(String query) takes a query and opens a HttpURLConnection,
    // throws exceptions if the URL is not good or IOException if it doesn't connect
    public HttpURLConnection openConnection(String query){
        URL url = null;

        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Encoding", "");
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
        } catch (IOException e) {
            Log.e(ACTIVITY_NAME, "Error with URL connection");
        }
        return conn;
    }

    public void close(){
        if (conn != null){
            conn.disconnect();
        }
    }

    public String postPost(Post post){
        String response = "";
        String query = makeQuery(NEW_POST);

        HttpURLConnection connection = null;

        URL url = null;

        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
        } catch (IOException e) {
            Log.e(ACTIVITY_NAME, "Error with URL connection");
        }

        try{
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(post.toJSON().toString());
            wr.flush();
            wr.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = "Posted!";
        }
        else {
            response = "Oops something went wrong…";

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    Log.i(ACTIVITY_NAME, response);
    return response;
    }

    public ArrayList<String> makeCommentsArray(JsonReader reader) throws IOException {
        ArrayList<String> comments = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()){
            comments.add(reader.nextString());
        }
        reader.endArray();

        return comments;

    }

    //readFood takes a JsonReader object and returns Food objects with the data from the JSON,
    //throws IOException if there are any errors with reading
    public Post readPost(JsonReader reader) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        String field;
        
        String id = "";
        String title = "";
        String category = "";
        String photoURL = "";
        String description = "";
        int upvotes = 0;
        Date createdAt = null;
        ArrayList<String> comments = null;

        reader.beginObject();
        while (reader.hasNext()) {
            field = reader.nextName();
            if(field.equals(ID)) {
                id = reader.nextString();
            } else if (field.equals(TITLE)) {
                title = reader.nextString();
            } else if (field.equals(CATEGORY)){
                category = reader.nextString();
            }else if (field.equals(PHOTO)){
                photoURL = reader.nextString();
            }else if (field.equals(DESCRIPTION)){
                description = reader.nextString();
            }else if (field.equals(CREATED_AT)){
                try {
                    createdAt = df.parse(reader.nextString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if(field.equals(UPVOTES)){
                upvotes = reader.nextInt();
            } else if(field.equals(COMMENTS)){
                comments = makeCommentsArray(reader);
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Post(id, title, category, photoURL, description, upvotes, createdAt, comments);
    }

    //makeFoodArray(JsonReader reader) takes a JsonReader and reads food arrays from the JSON
    // and returns an ArrayList<Food>
    public ArrayList<Post> getAllPosts( HttpURLConnection conn)  {
        ArrayList<Post> posts = new ArrayList<>();
        JsonReader reader;

        try {
            reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            reader.beginArray();
            while(reader.hasNext()){
                posts.add(readPost(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    //post image to cloudinary get back string url to store in db
    public String postToCloudinary(Context ctx, File photo) throws IOException {
        Log.i(ACTIVITY_NAME, photo.getName());
        Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(ctx));
        String getURL = cloudinary.uploader().upload(photo.getAbsolutePath(), ObjectUtils.emptyMap()).get("secure_url").toString();
        cloudinary.url().generate(photo.getName());

        Log.i(ACTIVITY_NAME, getURL);
        Log.i(ACTIVITY_NAME, "AFTER CLOUDINARY");
        return getURL;
    }

    public String postUpvote(Post post){

        String response = "";
        String query = makeQuery(UPVOTE);
        query = query + "/" + post.getId();

        Log.i(ACTIVITY_NAME, "Post id " + post.getId());
        Log.i(ACTIVITY_NAME, query);

        HttpURLConnection connection = null;

        URL url = null;

        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
        } catch (IOException e) {
            Log.e(ACTIVITY_NAME, "Error with URL connection");
        }

        try{
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            Log.i(ACTIVITY_NAME, post.toJSON().get("upvotes").toString());
            String votes = post.toJSON().get("upvotes").toString();

            String upvote = "{\"upvotes\":\""+votes+"\"}";

            Log.i(ACTIVITY_NAME, upvote);

            wr.writeBytes(upvote);
            wr.flush();
            wr.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = "Liked!";
            }
            else {
                response = "Oops something went wrong…";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(ACTIVITY_NAME, response);
        return response;

    }



}
