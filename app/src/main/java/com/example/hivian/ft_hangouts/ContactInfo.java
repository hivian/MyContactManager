package com.example.hivian.ft_hangouts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.hivian.ft_hangouts.DbBitmapUtility.getImage;

public class ContactInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            ImageView imageView = (ImageView) findViewById(R.id.info_image);
            TextView textView1 = (TextView) findViewById(R.id.info_name);
            TextView textView2 = (TextView) findViewById(R.id.info_lastName);
            TextView textView3 = (TextView) findViewById(R.id.info_phone);
            TextView textView4 = (TextView) findViewById(R.id.info_email);
            TextView textView5 = (TextView) findViewById(R.id.info_address);

            //Bitmap imageBm = getImage(contact.getImage());
           // byte[] imageBytes = Base64.decode(contact.getImage()b, Base64.DEFAULT);
            //Bitmap pic = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
           // imageView.setImageBitmap(pic);
            //if (imageBm != null)
             //   imageView.setImageBitmap(pic);
            textView1.setText(contact.getName());
            textView2.setText(contact.getLastName());
            textView3.setText(contact.getPhone());
            textView4.setText(contact.getEmail());
            textView5.setText(contact.getAddress());

            //textView1.setSelected(true);
        }
    }
}
