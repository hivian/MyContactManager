package com.example.hivian.my_contact_manager.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.views.fragments.ContactInfoFragment;
import com.example.hivian.my_contact_manager.views.fragments.ContactListFragment;


public class MainActivity extends AppCompatActivity implements ContactListFragment.DataPassListener {

    private ContactListFragment fragmentA;
    private ContactInfoFragment fragmentB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (findViewById(R.id.fragment_holder) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragmentA = new ContactListFragment();
            fragmentB = new ContactInfoFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_holder, fragmentA)
                    .commit();

        }
    }

    @Override
    public void passData(Contact contact) {
        Bundle args = new Bundle();
        args.putSerializable("contact", contact);
        fragmentB.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_holder, fragmentB)
                .addToBackStack(null)
                .commit();
    }

}
