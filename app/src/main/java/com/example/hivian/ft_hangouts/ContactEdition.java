package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ContactEdition extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static Boolean isImageLoaded = false;
    private static Boolean wasInBackground = false;
    private static String backgroundTime;
    private ImageView imageView;
    private TextView name;
    private TextView lastName;
    private TextView phone;
    private TextView email;
    private TextView address;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edition);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.edit_contact)  + "</font>"));

        if (MainActivity.getPurple()) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_purple)));
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

        contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            imageView = (ImageView) findViewById(R.id.edit_image);
            name = (TextView) findViewById(R.id.edit_name);
            lastName = (TextView) findViewById(R.id.edit_lastName);
            phone = (TextView) findViewById(R.id.edit_phone);
            email = (TextView) findViewById(R.id.edit_email);
            address = (TextView) findViewById(R.id.edit_address);

            if (contact.getImage() != null) {
                Bitmap imageBm = DbBitmapUtility.getImage(contact.getImage());
                imageView.setImageBitmap(imageBm);
            }
            name.setText(contact.getName());
            lastName.setText(contact.getLastName());
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
        MenuItem shareItemBlue = menu.findItem(R.id.action_blue);
        MenuItem shareItemPurple = menu.findItem(R.id.action_purple);

        shareItemSave.setVisible(true);
        shareItemBlue.setVisible(false);
        shareItemPurple.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_save) {
            saveEditionContact();
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onStop() {
        super.onStop();
        if (Utility.isAppInBackground(this)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            backgroundTime = sdf.format(new Date());
            wasInBackground = true;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        View parentLayout = findViewById(R.id.activity_contact_edition);

        if (backgroundTime != null && wasInBackground) {
            Snackbar.make(parentLayout, getResources().getString(R.string.alert_background)
                    + " " + backgroundTime, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            wasInBackground = false;
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

    public void saveEditionContact() {
        imageView = (ImageView) findViewById(R.id.edit_image);
        name = (TextView) findViewById(R.id.edit_name);
        lastName = (TextView) findViewById(R.id.edit_lastName);
        phone = (TextView) findViewById(R.id.edit_phone);
        email = (TextView) findViewById(R.id.edit_email);
        address = (TextView) findViewById(R.id.edit_address);

        if (name.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_name, Toast.LENGTH_LONG);
            toast.show();
        } else if (phone.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_phone, Toast.LENGTH_LONG);
            toast.show();
        } else {
            DBHandler db = new DBHandler(this);
            Contact contactEdit = db.getContact(contact.getId());

            Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            if (isImageLoaded && image != null)
                contactEdit.setImage(DbBitmapUtility.getBytes(image));
            else
                contactEdit.setImage(null);
            contactEdit.setName(name.getText().toString());
            contactEdit.setLastName(lastName.getText().toString());
            contactEdit.setPhone(phone.getText().toString());
            contactEdit.setEmail(email.getText().toString());
            contactEdit.setAddress(address.getText().toString());
            db.updateContact(contactEdit);

            MainActivity.getAdapter().notifyDataSetChanged();

            db.close();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
