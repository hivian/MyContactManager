package com.example.hivian.ft_hangouts;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.hivian.ft_hangouts.DbBitmapUtility.getBytes;

public class ContactEdition extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edition);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.edit_contact)  + "</font>"));

        extras = getIntent().getExtras();

        if (extras != null) {
            TextView name = (TextView) findViewById(R.id.edit_name);
            TextView lastName = (TextView) findViewById(R.id.edit_lastName);
            TextView phone = (TextView) findViewById(R.id.edit_phone);
            TextView email = (TextView) findViewById(R.id.edit_email);
            TextView address = (TextView) findViewById(R.id.edit_address);

            name.setText(extras.getString("name"));
            lastName.setText(extras.getString("lastName"));
            phone.setText(extras.getString("phone"));
            email.setText(extras.getString("email"));
            address.setText(extras.getString("address"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.edit_image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //isImageLoaded = true;
        }
    }

    public void browseEditionFolder(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void saveEditionContact(View view) {
        DBHandler db = new DBHandler(this);

        TextView name = (TextView) findViewById(R.id.edit_name);
        TextView lastName = (TextView) findViewById(R.id.edit_lastName);
        TextView phone = (TextView) findViewById(R.id.edit_phone);
        TextView email = (TextView) findViewById(R.id.edit_email);
        TextView address = (TextView) findViewById(R.id.edit_address);

        Contact contact = db.getContactByName(extras.getString("name"));

        contact.setName(name.getText().toString());
        contact.setLastName(lastName.getText().toString());
        contact.setPhone(phone.getText().toString());
        contact.setEmail(email.getText().toString());
        contact.setAddress(address.getText().toString());
        db.updateContact(contact);

        MainActivity.getAdapter().notifyDataSetChanged();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
