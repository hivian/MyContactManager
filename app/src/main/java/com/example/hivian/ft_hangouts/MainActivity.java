package com.example.hivian.ft_hangouts;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    public final static String EXTRA_MESSAGE = "com.example.hivian.ft_hangouts.MESSAGE";
    private static Boolean isPurple = false;
    private static Boolean wasInBackground = false;
    private static String backgroundTime;
    private static CustomAdapter adapter;

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
        //this.deleteDatabase("DB");
        final DBHandler db = new DBHandler(this);

        /*for (int i = 0; i < 1; i++) {
            SmsContent s = new SmsContent("Header" + String.valueOf(4), "Content" + String.valueOf(4), 2);
            db.addSms(s);
        }*/

        /*List<SmsContent> content = db.getAllSmsFromContact(2);

        for (SmsContent s : content) {
            Log.d("BLA", s.getHeader() + " - " + s.getContent() + " - " + s.getContactId());
        }*/

        //db.deleteAllSms(db);
        //this.deleteDatabase("DB");
        ListView listView;

        List<Contact> contacts = db.getAllContacts();
        //db.deleteAllContacts(db);
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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
        if (isAppInBackground()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            backgroundTime = sdf.format(new Date());
            Log.d("DEBUG", backgroundTime);
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
        Log.d("DEBUG", "CREATE CONTACT");
        Intent intent = new Intent(this, ContactCreation.class);

        startActivity(intent);
    }

    private boolean isAppInBackground() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);

        if (!tasks.isEmpty()) {
            final ComponentName topActivity = tasks.get(0).topActivity;
            return !topActivity.getPackageName().equals(getPackageName());
        }
        return false;
    }
}
