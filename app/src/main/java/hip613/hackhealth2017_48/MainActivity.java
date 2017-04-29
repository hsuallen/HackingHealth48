package hip613.hackhealth2017_48;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import hip613.hackhealth2017_48.models.Post;

public class MainActivity extends AppCompatActivity {
    final private String actionBarTitle = "Earth is Green!";
    final private String[] pics = new String[]{"http://res.cloudinary.com/allenhsu/image/upload/v1493474450/cup.jpg",
            "http://res.cloudinary.com/allenhsu/image/upload/v1493474450/juice.jpg"};

    protected ArrayList<Post> posts;
    protected int pos;
    protected ListView feed;
    protected TextView welcomeMsg, desc;

    protected class FeedFetcher extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String ...args) {
            return null;
        }

        protected void onPostExecute(String s) {
            setVisibility(View.VISIBLE);
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

//            ImageView image = (ImageView)result.findViewById(R.id.feed_image);
//            TextView title = (TextView)result.findViewById(R.id.feed_title);
//            TextView likes = (TextView)result.findViewById(R.id.feed_likes);
//
//            image.setImageResource(getItem(pos).getImageID());
//            title.setText(getItem(pos).getTitle());
//            likes.setText(getItem(pos).getLikes());

            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(actionBarTitle);

        welcomeMsg = (TextView)findViewById(R.id.textView);
        desc = (TextView)findViewById(R.id.textView2);
        feed = (ListView)findViewById(R.id.feed);

//        setVisibility(View.INVISIBLE);
        hideErnie();
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