package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ContactInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        getSupportActionBar().setTitle("Options");

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            ImageView imageView = (ImageView) findViewById(R.id.info_image);
            TextView textView1 = (TextView) findViewById(R.id.info_name);
            TextView textView2 = (TextView) findViewById(R.id.info_lastName);
            TextView textView3 = (TextView) findViewById(R.id.info_phone);
            TextView textView4 = (TextView) findViewById(R.id.info_email);
            TextView textView5 = (TextView) findViewById(R.id.info_address);

            if (contact.getImage() != null) {
                Bitmap imageBm = DbBitmapUtility.getImage(contact.getImage());
                imageView.setImageBitmap(imageBm);
                Log.d("BITMAPED", imageBm.toString());
            }
            textView1.setText(contact.getName());
            textView2.setText(contact.getLastName());
            textView3.setText(contact.getPhone());
            textView4.setText(contact.getEmail());
            textView5.setText(contact.getAddress());
        }
    }

    public void toSmsManager(View view) {
        Intent intent = new Intent(this, ContactSms.class);
        startActivity(intent);
    }

    public void callContact(View view) {
        TextView phone = (TextView) findViewById(R.id.info_phone);

        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + phone.getText().toString()));
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void editContact(View view) {
        TextView name = (TextView) findViewById(R.id.info_name);
        TextView lastName = (TextView) findViewById(R.id.info_lastName);
        TextView phone = (TextView) findViewById(R.id.info_phone);
        TextView email = (TextView) findViewById(R.id.info_email);
        TextView address = (TextView) findViewById(R.id.info_address);

        Intent intent = new Intent(this, ContactEdition.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("lastName", lastName.getText().toString());
        intent.putExtra("phone", phone.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("address", address.getText().toString());
        startActivity(intent);
    }

    public void deleteContact(View view) {
        final DBHandler db = new DBHandler(this);

        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.alert_delete_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.alert_delete_ok),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        TextView textView = (TextView) findViewById(R.id.info_name);
                        Log.d("DEBUG", textView.getText().toString());
                        Contact contact = db.getContactByName(textView.getText().toString());
                        db.deleteContact(contact);
                        MainActivity.getAdapter().notifyDataSetChanged();

                        Intent intent = new Intent(ContactInfo.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.alert_delete_cancel), null)
                .show();
    }
}
