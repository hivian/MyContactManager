package com.example.hivian.ft_hangouts;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.hivian.ft_hangouts.DbBitmapUtility.getBytes;

public class ContactEdition extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static Boolean isImageLoaded = false;
    private ImageView imageView;
    private TextView name;
    private TextView lastName;
    private TextView phone;
    private TextView email;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edition);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.edit_contact)  + "</font>"));

        Button button = (Button)findViewById(R.id.edit_button_save);
        if (MainActivity.getPurple()) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_purple)));
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

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

            imageView.setImageBitmap(decodeSampledBitmapFromResource(picturePath, 100, 100));
            isImageLoaded = true;
        }
    }

    public void browseEditionFolder(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void saveEditionContact(View view) {
        DBHandler db = new DBHandler(this);

        imageView = (ImageView) findViewById(R.id.edit_image);
        name = (TextView) findViewById(R.id.edit_name);
        lastName = (TextView) findViewById(R.id.edit_lastName);
        phone = (TextView) findViewById(R.id.edit_phone);
        email = (TextView) findViewById(R.id.edit_email);
        address = (TextView) findViewById(R.id.edit_address);

        Contact contact = db.getContactByName(name.getText().toString());

        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        if (isImageLoaded && image != null) {
            contact.setImage(DbBitmapUtility.getBytes(image));
            isImageLoaded = false;
        } else {
            contact.setImage(null);
        }
        contact.setName(name.getText().toString());
        contact.setLastName(lastName.getText().toString());
        contact.setPhone(phone.getText().toString());
        contact.setEmail(email.getText().toString());
        contact.setAddress(address.getText().toString());
        db.updateContact(contact);

        MainActivity.getAdapter().notifyDataSetChanged();

        db.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
