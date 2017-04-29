package hip613.hackhealth2017_48.api;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import javax.net.ssl.HttpsURLConnection;

import hip613.hackhealth2017_48.R;
import hip613.hackhealth2017_48.models.Post;

public class AppAPIHelper {
    //ACTIVITY_NAME for debugging, queryURL is the URL for the API
    private final String ACTIVITY_NAME = "AppAPIHelper";
    private final String BASE = "https://aqueous-beach-97404.herokuapp.com/";
    private final String NEW_POST = "api/posts/newPost";
    private final String GET_ALL = "api/posts/getAll";
    private final String UPVOTE = "api/posts/upvote";
    private final String REMOVE = "api/posts/removePost";
    private final String GET_BY_ID = "api/posts/getByID";
    //post field names
    private final String ID = "id";
    private final String TITLE = "title";
    private final String CATEGORY = "category";
    private final String PHOTO = "photo";
    private final String DESCRIPTION = "description";
    private final String CREATED_AT = "createdAt";
    private final String COMMENTS = "comments";

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

        HttpURLConnection conn = null;
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

    public String postPost(HttpURLConnection conn, Post post){
        String response = "";

        conn.setDoInput(true);
        conn.setDoOutput(true);

        try{
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(post.toJSON());

        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        }
        else {
            response = "Oops something went wrong…";

        }
    } catch (Exception e) {
        e.printStackTrace();
    }

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
            } else if(field.equals(UPVOTE)){
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
    public ArrayList<Post> makePostArray(JsonReader reader) throws IOException {
        ArrayList<Post> posts = new ArrayList<>();
        reader.beginArray();
        while(reader.hasNext()){
            posts.add(readPost(reader));
        }
        reader.endArray();

        return posts;
    }

    //searchFood(HttpURLConnection conn) takes in a HttpURLConnection and queries the API
    // for JSON results
    public ArrayList<Post> searchFood(HttpURLConnection conn) {
        ArrayList<Post> posts = null;
        JsonReader reader;
        String name = "";

        try {
            reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if(name.equals("foods")){
                    posts = makePostArray(reader);
                }else{
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

}
