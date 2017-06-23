package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.szugyi.circlemenu.view.CircleImageView;
import com.szugyi.circlemenu.view.CircleLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ContactInfo extends AppCompatActivity implements View.OnClickListener {
    private static Boolean wasInBackground = false;
    private static String backgroundTime;
    private ImageView imageView;
    private TextView name;
    private TextView phone;
    private TextView email;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        getSupportActionBar().setTitle("Options");

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            imageView = (ImageView) findViewById(R.id.info_image);
            name = (TextView) findViewById(R.id.info_name);
            phone = (TextView) findViewById(R.id.info_phone);
            email = (TextView) findViewById(R.id.info_email);
            address = (TextView) findViewById(R.id.info_address);

            if (contact.getImage() != null) {
                Bitmap imageBm = DbBitmapUtility.getImage(contact.getImage());
                imageView.setImageBitmap(imageBm);
            }
            name.setText(contact.getName());
            phone.setText(contact.getPhone());
            if (email.getText().equals("")) {
                email.setTypeface(null, Typeface.ITALIC);
                email.setTextColor(Color.GRAY);
                email.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                email.setText(R.string.placeholder_none);
            }
            else
                email.setText(contact.getEmail());
            if (address.getText().equals("")) {
                address.setTypeface(null, Typeface.ITALIC);
                address.setTextColor(Color.GRAY);
                address.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                address.setText(R.string.placeholder_none);
            }
            else
                address.setText(contact.getAddress());

        }

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_contact_info);
        scrollView.getChildAt(0).setOnClickListener(this);

        CircleLayout circleLayout = (CircleLayout) findViewById(R.id.circle_layout);
        circleLayout.setOnClickListener(this);

    }

    public void onClick(View v) {
        // do something when the button is clicked
        Log.d("DEBUG", "TOUCHED");
        CircleLayout circleLayout = (CircleLayout) findViewById(R.id.circle_layout);
        circleLayout.setVisibility(View.VISIBLE);
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
        View parentLayout = findViewById(R.id.activity_contact_info);

        if (backgroundTime != null && wasInBackground) {
            Snackbar.make(parentLayout, getResources().getString(R.string.alert_background)
                    + " " + backgroundTime, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            wasInBackground = false;
        }
    }

    public void toSmsManager(View view) {
        name = (TextView) findViewById(R.id.info_name);
        phone = (TextView) findViewById(R.id.info_phone);

        Intent intent = new Intent(this, ContactSms.class);

        intent.putExtra("name", name.getText().toString());
        intent.putExtra("phone", phone.getText().toString());
        startActivity(intent);
    }

    public void callContact(View view) {
        phone = (TextView) findViewById(R.id.info_phone);
        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + phone.getText().toString()));
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            Toast toast = Toast.makeText(this, R.string.alert_no_call_perm, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void editContact(View view) {
        DBHandler db = new DBHandler(this);

        name = (TextView) findViewById(R.id.info_name);

        Contact contact = db.getContactByName(name.getText().toString());
        Intent intent = new Intent(this, ContactEdition.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    public void deleteContact(View view) {
        final DBHandler db = new DBHandler(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.alert_delete_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.alert_delete_ok),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        TextView textView = (TextView) findViewById(R.id.info_name);
                        Log.d("DEBUG", textView.getText().toString());
                        Contact contact = db.getContactByName(textView.getText().toString());
                        db.deleteContact(contact);
                        List<SmsContent> allSms = db.getAllSmsFromContact(contact.getId());
                        for (SmsContent sms : allSms) {
                            db.deleteSms(sms);
                        }
                        MainActivity.getAdapter().notifyDataSetChanged();

                        Intent intent = new Intent(ContactInfo.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.alert_delete_cancel), null)
                .show();
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    public void cancelMenu(View view) {
        CircleLayout circleLayout = (CircleLayout) findViewById(R.id.circle_layout);
        circleLayout.setVisibility(View.GONE);
    }
}
