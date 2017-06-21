package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.hivian.ft_hangouts.MESSAGE";
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private static Boolean isPurple = false;
    private static Boolean wasInBackground = false;
    private static String backgroundTime;
    private static CustomAdapter adapter;
    private FloatingActionButton fab;

    public static Boolean getPurple() {
        return isPurple;
    }

    public void setPurple(Boolean bool) {
        this.isPurple = bool;
    }

    public static CustomAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>Contacts</font>"));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (getPurple()) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_purple)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_purple)));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

        //this.deleteDatabase("DB");
        final DBHandler db = new DBHandler(this);
        //db.deleteAllContacts(db);
        //db.deleteAllSms(db);

        ListView listView;

        List<Contact> contacts = db.getAllContacts();

        ArrayList <List <String>> allData = new ArrayList<>();

        for (Contact cont : contacts) {
            ArrayList<String> elem = new ArrayList<>();
            elem.add(cont.getName());
            elem.add(cont.getPhone());
            allData.add(elem);
        }

        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter (this, allData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textName = (TextView) view.findViewById(R.id.row_name);
                String name = textName.getText().toString();

                Contact contact = db.getContactByName(name);

                Intent intent = new Intent(MainActivity.this, ContactInfo.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
            }
        });
        db.close();
        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkStoragePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int checkSendSmsPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS);
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE);
            if (checkStoragePermission + checkSendSmsPermission + checkCallPhonePermission
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.CALL_PHONE},
                            PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_blue) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            setPurple(false);
            return true;
        }
        if (id == R.id.action_purple) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_purple)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_purple)));
            setPurple(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        View parentLayout = findViewById(R.id.content_main);

        if (backgroundTime != null && wasInBackground) {
            Snackbar.make(parentLayout, getResources().getString(R.string.alert_background)
                    + " " + backgroundTime, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            wasInBackground = false;
        }
    }

    public void createContact(View view) {
        Intent intent = new Intent(this, ContactCreation.class);

        startActivity(intent);
    }
}
