package hip613.hackhealth2017_48;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by allenhsu on 2017-04-29.
 */

public class DetailsFragment extends Fragment {

    protected View gui;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bun = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gui = inflater.inflate(R.layout.post_details, null);

        return gui;
    }
}