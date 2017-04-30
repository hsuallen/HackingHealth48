package hip613.hackhealth2017_48;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import hip613.hackhealth2017_48.api.AppAPIHelper;
import hip613.hackhealth2017_48.models.Post;

public class MainActivity extends AppCompatActivity {
    final private String ACTIVITY_NAME="MainActivity";
    final private String actionBarLoading = "Loading Posts...";
    final private String actionBarTitle = "Ernie";

    public static ArrayList<Bitmap> feedImages = new ArrayList<>();
    public static int pos;

    private ArrayList<Post> posts;
    private boolean isTablet;
    private ListView feed;
    private TextView welcomeMsg, desc;
    private FeedAdapter adapter;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posts = new ArrayList<>();
        adapter = new FeedAdapter(this);

        getSupportActionBar().setTitle(actionBarLoading);
        getSupportActionBar().hide();

        welcomeMsg = (TextView)findViewById(R.id.textView);
        welcomeMsg.setTextSize(40);
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
                    Log.i(ACTIVITY_NAME, "Why is this null" + posts.get(pos).getId());
                    intent.putExtra("id", posts.get(pos).getId());
                    intent.putExtra("title", posts.get(pos).getTitle());
                    intent.putExtra("upvotes", posts.get(pos).getUpvotes());
                    intent.putExtra("category", posts.get(pos).getCategory());
                    intent.putExtra("description", posts.get(pos).getDescription());
                    intent.putExtra("imageURL", posts.get(pos).getPhotoURL());

                    startActivityForResult(intent, 5);
                }
            }
        });

        swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedImages = new ArrayList<>();
                FeedFetcher refresh = new FeedFetcher();
                refresh.execute();
                swipe.setRefreshing(false);
            }
        });

        setVisibility(View.INVISIBLE);

        FeedFetcher async = new FeedFetcher();
        async.execute();

        isTablet = (findViewById(R.id.frameLayout) != null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FeedFetcher fetcher = new FeedFetcher();
        fetcher.execute();
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
                startActivityForResult(new Intent(MainActivity.this, PostActivity.class), 5);
                break;
            case R.id.profile:
                startActivityForResult(new Intent(MainActivity.this, Profile.class), 5);
                break;
            case R.id.settings:
                break;
            case R.id.logout:
                break;
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
        swipe.setVisibility(visibility);
        feed.setVisibility(visibility);
    }

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

            welcomeMsg.setText(R.string.welcome);
            welcomeMsg.setTextSize(20);

            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().show();
            hideErnie();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            feedImages.add(mIcon11);
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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

            new DownloadImageTask(image).execute(getItem(pos).getPhotoURL());
            title.setText(getItem(pos).getTitle());
            likes.setText(String.valueOf(getItem(pos).getUpvotes()));

            return result;
        }
    }
}