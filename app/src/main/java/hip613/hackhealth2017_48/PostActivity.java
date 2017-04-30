package hip613.hackhealth2017_48;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hip613.hackhealth2017_48.api.AppAPIHelper;
import hip613.hackhealth2017_48.models.Post;

public class PostActivity extends AppCompatActivity {
    private final String ACTIVITY_NAME = "PostActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File photoFile;
    private Post post;
    private ProgressBar progressBar;
    private Spinner categoryField;
    private EditText titleField, descriptionField;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            try {
//                photoFile = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if(photoFile != null){
//                Log.i(ACTIVITY_NAME, "photo file made");
//                Log.i(ACTIVITY_NAME, photoFile.getAbsolutePath());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(photoFile));
//            }

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "capturedImage";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        return image;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Calendar c = Calendar.getInstance();

            photoFile = new File(c.getTimeInMillis() + ".png");

            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(photoFile.getName(), Context.MODE_PRIVATE);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.i(ACTIVITY_NAME, "File written");
            } catch (Exception e) {
                Log.e(ACTIVITY_NAME, "File not found");
            }

            Log.i(ACTIVITY_NAME, String.valueOf(getBaseContext().getFileStreamPath(photoFile.getName())));

            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setTitle("Create a New Post");


        titleField = (EditText)findViewById(R.id.editText2);
        descriptionField = (EditText)findViewById(R.id.editText3);
        categoryField = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryField.setAdapter(adapter);

        dispatchTakePictureIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.post, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();

        switch (id) {
            case R.id.post:
                postHandler();
        }

        return true;
    }

    private class CloudinaryPost extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {

            AppAPIHelper api = new AppAPIHelper();
            String photoURL = "";
            File photo = getBaseContext().getFileStreamPath(photoFile.getName());
            try {
                photoURL = api.postToCloudinary(getBaseContext(), photo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return photoURL;
        }

        @Override
        protected void onPostExecute(String photoURL) {
            super.onPostExecute(photoURL);

            String title = titleField.getText().toString();
            Log.i(ACTIVITY_NAME, title);
            String category = categoryField.getSelectedItem().toString();
            String description = descriptionField.getText().toString();

            post = new Post(title, category, photoURL, description);

            PostToServer postToServer = new PostToServer();

            postToServer.execute();


        }
    }

    private class PostToServer extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            if(post != null){
                AppAPIHelper api = new AppAPIHelper();
                response = api.postPost(post);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Toast toast = Toast.makeText(getBaseContext(), response , Toast.LENGTH_SHORT); //this is the ListActivity
            toast.show();

            finish();
        }
    }

    public void postHandler(){

        CloudinaryPost cp = new CloudinaryPost();
        cp.execute();

    }
}
