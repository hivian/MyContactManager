package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;


public class ContactInfo extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private TextView name;
    private TextView phone;
    private FloatingActionMenu actionMenu;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Options");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Utility.changeStatusBarColor(this);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            ImageView imageView = (ImageView) findViewById(R.id.info_image);
            name = (TextView) findViewById(R.id.info_name);
            phone = (TextView) findViewById(R.id.info_phone);
            TextView email = (TextView) findViewById(R.id.info_email);
            TextView address = (TextView) findViewById(R.id.info_address);

            if (contact.getImage() != null) {
                Bitmap imageBm = DbBitmapUtility.getImage(contact.getImage());
                imageView.setImageBitmap(imageBm);
            }
            name.setText(contact.getName());
            phone.setText(contact.getPhone());
            if (contact.getEmail().equals("")) {
                email.setTypeface(null, Typeface.ITALIC);
                email.setTextColor(Color.GRAY);
                email.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                email.setText(R.string.placeholder_none);
            }
            else
                email.setText(contact.getEmail());
            if (contact.getAddress().equals("")) {
                address.setTypeface(null, Typeface.ITALIC);
                address.setTextColor(Color.GRAY);
                address.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                address.setText(R.string.placeholder_none);
            }
            else
                address.setText(contact.getAddress());
        }
        initInfoMenu();
        scrollView = (ScrollView) findViewById(R.id.scrollview_contact_info);
        scrollView.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        actionMenu.close(true);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemId1:
                toSmsManager();
                break;
            case R.id.itemId2:
                callContact();
                break;
            case R.id.itemId3:
                editContact();
                break;
            case R.id.itemId4:
                deleteContact();
                break;
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initInfoMenu() {
        ImageView menuIcon1 = new ImageView(this);
        ImageView menuIcon2 = new ImageView(this);
        ImageView menuIcon3 = new ImageView(this);
        ImageView menuIcon4 = new ImageView(this);

        menuIcon1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message_black_75dp, null));
        menuIcon2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_call_black_75dp, null));
        menuIcon3.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mode_edit_black_75dp, null));
        menuIcon4.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_black_75dp, null));
        menuIcon1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon1.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        menuIcon2.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        menuIcon3.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        menuIcon4.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        menuIcon1.setPadding(15,15,15,15);
        menuIcon2.setPadding(15,15,15,15);
        menuIcon3.setPadding(15,15,15,15);
        menuIcon4.setPadding(15,15,15,15);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(160,160));

        ImageView menuInfo = (ImageView) findViewById(R.id.info_menu);
        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(itemBuilder.setContentView(menuIcon1).build())
                .addSubActionView(itemBuilder.setContentView(menuIcon2).build())
                .addSubActionView(itemBuilder.setContentView(menuIcon3).build())
                .addSubActionView(itemBuilder.setContentView(menuIcon4).build())
                .attachTo(menuInfo)
                .setStartAngle(100)
                .setEndAngle(260)
                .build();

        menuIcon1.setId(R.id.itemId1);
        menuIcon1.setOnClickListener(this);
        menuIcon2.setId(R.id.itemId2);
        menuIcon2.setOnClickListener(this);
        menuIcon3.setId(R.id.itemId3);
        menuIcon3.setOnClickListener(this);
        menuIcon4.setId(R.id.itemId4);
        menuIcon4.setOnClickListener(this);
    }

    private void toSmsManager() {
        name = (TextView) findViewById(R.id.info_name);
        phone = (TextView) findViewById(R.id.info_phone);

        Intent intent = new Intent(this, ContactSms.class);

        intent.putExtra("name", name.getText().toString());
        intent.putExtra("phone", phone.getText().toString());
        startActivity(intent);
    }

    private void callContact() {
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

    private void editContact() {
        DBHandler db = new DBHandler(this);

        name = (TextView) findViewById(R.id.info_name);

        Contact contact = db.getContactByName(name.getText().toString());
        Intent intent = new Intent(this, ContactEdition.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    private void deleteContact() {
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
}
