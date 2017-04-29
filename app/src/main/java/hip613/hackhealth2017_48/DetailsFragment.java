package hip613.hackhealth2017_48;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by allenhsu on 2017-04-29.
 */

public class DetailsFragment extends Fragment {
    private int upvotes;
    private String title, category, description;
    private TextView titleTV, categoryTV, descTV;
    private View gui;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bun = getArguments();

        upvotes = bun.getInt("likes");
        title = bun.getString("title");
        category = bun.getString("category");
        description = bun.getString("description");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gui = inflater.inflate(R.layout.post_details, null);

        titleTV = (TextView)gui.findViewById(R.id.textView7);
        categoryTV = (TextView)gui.findViewById(R.id.textView9);
        descTV = (TextView)gui.findViewById(R.id.textView11);

        titleTV.setText(title);
        categoryTV.setText(category);
        descTV.setText(description);

        return gui;
    }
}