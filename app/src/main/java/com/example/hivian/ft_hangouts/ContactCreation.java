package com.example.hivian.ft_hangouts;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.hivian.ft_hangouts.DbBitmapUtility.getBytes;


public class ContactCreation extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static Boolean wasInBackground = false;
    private static String backgroundTime;
    private static Boolean isImageLoaded = false;
    private ImageView imageView;
    private EditText name;
    private EditText lastName;
    private EditText phone;
    private EditText email;
    private EditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_creation);

        Button button = (Button)findViewById(R.id.button_save);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.create_contact)  + "</font>"));

        if (MainActivity.getPurple()) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_purple)));
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            imageView.setImageResource(android.R.drawable.ic_menu_camera);
            isImageLoaded = false;
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.post(new Runnable() {
                @Override
                public void run()
                {
                    imageView.setImageBitmap(decodeSampledBitmapFromResource(picturePath, 100, 100));
                    Toast.makeText(ContactCreation.this, R.string.alert_image_load, Toast.LENGTH_SHORT).show();
                }
            });
            isImageLoaded = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isAppInBackground()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            backgroundTime = sdf.format(new Date());
            wasInBackground = true;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        View parentLayout = findViewById(R.id.activity_contact_creation);

        if (backgroundTime != null && wasInBackground) {
            Snackbar.make(parentLayout, getResources().getString(R.string.alert_background)
                    + " " + backgroundTime, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            wasInBackground = false;
        }
    }

    public void browseFolder(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void saveContact(View view) {
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        byte[] imageDb;

        name = (EditText)findViewById(R.id.name);
        lastName = (EditText)findViewById(R.id.lastName);
        phone = (EditText)findViewById(R.id.phone);
        email = (EditText)findViewById(R.id.email);
        address = (EditText)findViewById(R.id.address);

        if (isImageLoaded) {
            imageDb = getBytes(image);
            isImageLoaded = false;
        }
        else
            imageDb = null;
        if (name.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_name, Toast.LENGTH_LONG);
            toast.show();
        } else if (phone.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_phone, Toast.LENGTH_LONG);
            toast.show();
        } else {
            DBHandler db = new DBHandler(this);

            if (db.isDuplicate(db, name.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.alert_duplicates, Toast.LENGTH_LONG);
                toast.show();
                return ;
            }
            db.addContact(new Contact(imageDb, name.getText().toString(), lastName.getText().toString(),
                    phone.getText().toString(), email.getText().toString(), address.getText().toString()));

            List<Contact> contacts = db.getAllContacts();
            for (Contact cont : contacts) {
                String log = "Id: " + cont.getId() + " ,Name: " + cont.getName() + " ,LastName: " + cont.getLastName() + " ,Phone: " + cont.getPhone() + " ,Email: " + cont.getEmail() + " ,Address: " + cont.getAddress();
                Log.d("Contact: : ", log);
            }

            db.close();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    private boolean isAppInBackground() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);

        if (!tasks.isEmpty()) {
            final ComponentName topActivity = tasks.get(0).topActivity;
            return !topActivity.getPackageName().equals(getPackageName());
        }
        return false;
    }

    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
