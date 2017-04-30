package hip613.hackhealth2017_48;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hip613.hackhealth2017_48.api.AppAPIHelper;
import hip613.hackhealth2017_48.models.Post;

/**
 * Created by allenhsu on 2017-04-29.
 */

public class DetailsFragment extends Fragment {
    private int upvotes;
    private ImageView image;
    private String id, title, category, description, URL;
    private TextView titleTV, categoryTV, descTV;
    private View gui;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bun = getArguments();

        id = bun.getString("id");
        upvotes = bun.getInt("upvotes");
        title = bun.getString("title");
        category = bun.getString("category");
        description = bun.getString("description");
        URL = bun.getString("imageURL");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gui = inflater.inflate(R.layout.post_details, null);

        image = (ImageView)gui.findViewById(R.id.imageView3);
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Upvote upvote = new Upvote();
                upvote.execute();

                return false;
            }
        });
        titleTV = (TextView)gui.findViewById(R.id.textView7);
        categoryTV = (TextView)gui.findViewById(R.id.textView9);
        descTV = (TextView)gui.findViewById(R.id.textView11);

        image.setImageBitmap(MainActivity.feedImages.get(MainActivity.pos));
        titleTV.setText(title);
        categoryTV.setText(category);
        descTV.setText(description);

        return gui;
    }

    private class Upvote extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            AppAPIHelper api = new AppAPIHelper();
            Post post = new Post(title, category, URL, description);
            post.setUpvotes(upvotes);
            post.setId(id);

            return api.postUpvote(post);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Toast toast = Toast.makeText(getActivity(), response , Toast.LENGTH_SHORT); //this is the ListActivity
            toast.show();


        }
    }
}