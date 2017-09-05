package com.example.hivian.my_contact_manager.views.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.adapters.CustomAdapter;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.views.fragments.ContactInfoFragment;
import com.example.hivian.my_contact_manager.views.fragments.ContactListFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ContactListFragment fragmentA = (ContactListFragment) getFragmentManager().
               findFragmentById(R.id.contactListFragment);
        ContactInfoFragment fragmentB = (ContactInfoFragment) getFragmentManager().
                findFragmentById(R.id.contactInfoFragment);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .show(fragmentA)
                .hide(fragmentB)
                .commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Log.d("D", "TOTO");
            if (fragmentA != null && fragmentA.isInLayout()) {
                getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>Contacts</font>"));
                Log.d("D", "TOTO1");
            } else if (fragmentB != null && fragmentA.isInLayout()) {
                Log.d("D", "TOTO2");
                getSupportActionBar().setTitle("Options");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

   /* @Override
    public void passData(String data) {
        FragmentB fragmentB = new FragmentB ();
        Bundle args = new Bundle();
        args.putString(FragmentB.DATA_RECEIVE, data);
        fragmentB .setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragmentB )
                .commit();
    }*/

}
