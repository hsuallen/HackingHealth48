package hip613.hackhealth2017_48;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by allenhsu on 2017-04-29.
 */

public class FragTransaction extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");

        getSupportActionBar().setTitle(title);

        Fragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.frameLayout, fragment).commit();
    }
}
