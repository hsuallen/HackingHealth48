package hip613.hackhealth2017_48;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    final private String actionBarTitle = "Earth is Green!";
    final private String[] pics = new String[]{"http://res.cloudinary.com/allenhsu/image/upload/v1493474450/cup.jpg",
            "http://res.cloudinary.com/allenhsu/image/upload/v1493474450/juice.jpg"};

    protected TextView welcomeMsg, desc;
    protected ListView feed;

    class FeedFetcher extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String ...args) {
            return null;
        }

        protected void onPostExecute(String s) {
            setVisibility(View.VISIBLE);
            hideErnie();
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

        setVisibility(View.INVISIBLE);
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