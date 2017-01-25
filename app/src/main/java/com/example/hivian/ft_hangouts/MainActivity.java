package com.example.hivian.ft_hangouts;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.hivian.ft_hangouts.MESSAGE";
    private static Boolean isYellow = false;

    public static Boolean getYellow() {
        return isYellow;
    }

    public void setYellow(Boolean bool) {
        this.isYellow = bool;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DBHandler db = new DBHandler(this);

        //this.deleteDatabase("DB");
        ListView listview;

        List<Contact> contacts = db.getAllContacts();
        //db.deleteAllContacts(db);
        ArrayList <List <String>> allData = new ArrayList<>();

        for (Contact cont : contacts) {
            ArrayList<String> elem = new ArrayList<>();
            elem.add(cont.getName());
            elem.add(cont.getPhone());
            allData.add(elem);
        }

        listview = (ListView) findViewById(R.id.listView);
        CustomAdapter adapter = new CustomAdapter (this, allData);
        listview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_blue) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            setYellow(false);
            return true;
        }
        if (id == R.id.action_yellow) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorYellow)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorYellow)));
            setYellow(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEBUG", "PAUSED");
    }

    /*@Override
    public void onResume(){
        super.onResume();
        View parentLayout = findViewById(R.id.content_main);
        Snackbar.make(parentLayout, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        Log.d("DEBUG", "RESUME");
    }*/

    public void createContact(View view) {
        Log.d("DEBUG", "CREATE CONTACT");
        Intent intent = new Intent(this, ContactCreation.class);

        startActivity(intent);
    }

}
