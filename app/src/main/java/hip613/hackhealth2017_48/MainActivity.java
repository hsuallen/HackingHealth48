package hip613.hackhealth2017_48;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import hip613.hackhealth2017_48.api.AppAPIHelper;
import hip613.hackhealth2017_48.models.Post;

class HTTPUtils {
    public static Bitmap getImage(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public static Bitmap getImage(String urlString) {
        try {
            URL url = new URL(urlString);
            return getImage(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}

public class MainActivity extends AppCompatActivity {
    final private String ACTIVITY_NAME="MainActivity";
    final private String actionBarLoading = "Loading Posts...";
    final private String actionBarTitle = "Earth is Green!";
    final private String[] pics = new String[]{"http://res.cloudinary.com/allenhsu/image/upload/v1493474450/cup.jpg",
            "http://res.cloudinary.com/allenhsu/image/upload/v1493474450/juice.jpg"};

    private ArrayList<Post> posts;
    private boolean isTablet;
    private int pos;
    private ListView feed;
    private TextView welcomeMsg, desc;
    private FeedAdapter adapter;

    protected class FeedFetcher extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String ...args) {

            AppAPIHelper api = new AppAPIHelper();

            posts = api.getAllPosts(api.openConnection(api.makeQuery(api.GET_ALL)));

            Log.i(ACTIVITY_NAME, "query made");
            return null;
        }

        protected void onPostExecute(String s) {
            setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            getSupportActionBar().setTitle(actionBarTitle);
            hideErnie();
        }
    }

    protected class FeedAdapter extends ArrayAdapter<Post> {
        public FeedAdapter(Context ctx) { super(ctx, 0); }

        public int getCount() { return posts.size(); }
        public Post getItem(int pos) { return posts.get(pos); }
        public View getView(int pos, View convertView, ViewGroup Parent) {
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();

            int layout = R.layout.row_feed;
            View result = inflater.inflate(layout, null);

            ImageView image = (ImageView)result.findViewById(R.id.feed_image);
            TextView title = (TextView)result.findViewById(R.id.feed_title);
            TextView likes = (TextView)result.findViewById(R.id.feed_likes);

            Log.i(ACTIVITY_NAME, getItem(pos).getPhotoURL());
            image.setImageBitmap(HTTPUtils.getImage(getItem(pos).getPhotoURL()));
            title.setText(getItem(pos).getTitle());
            likes.setText(String.valueOf(getItem(pos).getUpvotes()));

            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posts = new ArrayList<>();
        adapter = new FeedAdapter(this);

        getSupportActionBar().setTitle(actionBarLoading);

        welcomeMsg = (TextView)findViewById(R.id.textView);
        desc = (TextView)findViewById(R.id.textView2);
        feed = (ListView)findViewById(R.id.feed);
        feed.setAdapter(adapter);

        feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;

                if (isTablet) {
                } else {
                    Intent intent = new Intent(MainActivity.this, FragTransaction.class);
                    intent.putExtra("title", posts.get(pos).getTitle());
                    intent.putExtra("likes", posts.get(pos).getUpvotes());
                    intent.putExtra("category", posts.get(pos).getCategory());
                    intent.putExtra("description", posts.get(pos).getDescription());

                    startActivityForResult(intent, 5);
                }
            }
        });

        setVisibility(View.INVISIBLE);

        FeedFetcher async = new FeedFetcher();
        async.execute();

        isTablet = (findViewById(R.id.frameLayout) != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.main_menu, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();

        switch (id) {
            case R.id.post:
                startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        return true;
    }

    private void hideErnie() {
        ImageView ernie = (ImageView)findViewById(R.id.ernie);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);

        ernie.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setVisibility(int visibility) {
        welcomeMsg.setVisibility(visibility);
        desc.setVisibility(visibility);
        feed.setVisibility(visibility);
    }
}