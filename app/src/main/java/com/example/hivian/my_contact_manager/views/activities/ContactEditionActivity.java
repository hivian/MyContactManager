package com.example.hivian.my_contact_manager.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.utilities.BitmapUtility;
import com.example.hivian.my_contact_manager.utilities.Utility;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.views.fragments.ContactListFragment;


public class ContactEditionActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static Boolean isImageLoaded = false;
    private ImageView imageView;
    private TextView name;
    private TextView phone;
    private TextView email;
    private TextView address;
    private Contact contact;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edition);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.edit_contact));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Utility.changeStatusBarColor(this);

        db = DBHandler.getInstance(this);

        contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            imageView = (ImageView) findViewById(R.id.edit_image);
            name = (TextView) findViewById(R.id.edit_name);
            phone = (TextView) findViewById(R.id.edit_phone);
            email = (TextView) findViewById(R.id.edit_email);
            address = (TextView) findViewById(R.id.edit_address);

            if (contact.getImage() != null) {
                Bitmap imageBm = BitmapUtility.getImage(contact.getImage());
                imageView.setImageBitmap(imageBm);
                isImageLoaded = true;
            }
            name.setText(contact.getName());
            phone.setText(contact.getPhone());
            email.setText(contact.getEmail());
            address.setText(contact.getAddress());
        }
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
                finish();
                return true;
            case R.id.action_save:
                saveEditionContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageView = (ImageView) findViewById(R.id.edit_image);
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
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageBitmap(Utility.decodeSampledBitmapFromResource(picturePath, 100, 100));
            isImageLoaded = true;
        }
    }

    public void browseEditionFolder(View view) {
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

    private void saveEditionContact() {
        imageView = (ImageView) findViewById(R.id.edit_image);
        name = (TextView) findViewById(R.id.edit_name);
        phone = (TextView) findViewById(R.id.edit_phone);
        email = (TextView) findViewById(R.id.edit_email);
        address = (TextView) findViewById(R.id.edit_address);
        byte[] imageDb;

        if (isImageLoaded) {
            Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            imageDb = BitmapUtility.getBytes(image);
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
            Contact contactEdit = db.getContact(contact.getId());

            if (!name.getText().toString().equals(contactEdit.getName())
                    && db.isDuplicate(db, name.getText().toString())) {
                Toast.makeText(this, R.string.alert_duplicates, Toast.LENGTH_LONG).show();
                return ;
            }
            if (email.getText().toString().trim().length() != 0
                    && !Utility.isValidEmail(email.getText())) {
                Toast.makeText(this, R.string.alert_invalid_email, Toast.LENGTH_LONG).show();
                return ;
            }
            db.updateContact(new Contact(imageDb, name.getText().toString(),
                    phone.getText().toString(), email.getText().toString(), address.getText().toString()));

            ContactListFragment.getAdapter().notifyDataSetChanged();

            Utility.hideKeyboard(this);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
