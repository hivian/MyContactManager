package com.example.hivian.my_contact_manager.views.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.utilities.Utility;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.views.fragments.ContactListFragment;

import static com.example.hivian.my_contact_manager.utilities.BitmapUtility.getBytes;


public class ContactCreationActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static Boolean isImageLoaded = false;
    private ImageView imageView;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_creation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.create_contact) + "</font>"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = DBHandler.getInstance(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        Utility.changeStatusBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem shareItemSave = menu.findItem(R.id.action_save);

        shareItemSave.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_save:
                saveContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                public void run() {
                    imageView.setImageBitmap(Utility.decodeSampledBitmapFromResource(picturePath, 100, 100));
                    Toast.makeText(ContactCreationActivity.this, R.string.alert_image_load, Toast.LENGTH_SHORT).show();
                }
            });
            isImageLoaded = true;
        }
    }

    public void browseFolder(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        } else {
            isImageLoaded = false;
            Toast toast = Toast.makeText(this, R.string.alert_no_read_perm, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void saveContact() {
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        byte[] imageDb;

        EditText name = (EditText) findViewById(R.id.name);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText email = (EditText) findViewById(R.id.email);
        EditText address = (EditText) findViewById(R.id.address);

        if (isImageLoaded) {
            imageDb = getBytes(image);
            isImageLoaded = false;
        } else {
            imageDb = null;
        }
        if (name.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_name, Toast.LENGTH_LONG);
            toast.show();
        } else if (phone.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_phone, Toast.LENGTH_LONG);
            toast.show();
        } else {
            if (db.isDuplicate(db, name.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.alert_duplicates, Toast.LENGTH_LONG);
                toast.show();
                return ;
            }
            db.addContact(new Contact(imageDb, name.getText().toString(),
                    phone.getText().toString(), email.getText().toString(), address.getText().toString()));

            Utility.hideKeyboard(this);
            ContactListFragment.getAdapter().notifyDataSetChanged();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
