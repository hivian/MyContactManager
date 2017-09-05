package com.example.hivian.my_contact_manager.views.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.adapters.CustomAdapter;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.utilities.Utility;
import com.example.hivian.my_contact_manager.views.activities.ContactCreationActivity;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hivian on 9/4/17.
 */

public class ContactListFragment extends Fragment implements View.OnClickListener {

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private static CustomAdapter adapter;
    public static CustomAdapter getAdapter() {
        return adapter;
    }
    private DBHandler db;

    private DataPassListener mCallback;

    public interface DataPassListener{
        void passData(Contact contact);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ab.setTitle(Html.fromHtml("<font color='white'>Contacts</font>" , Html.FROM_HTML_MODE_LEGACY));
            } else {
                ab.setTitle(Html.fromHtml("<font color='white'>Contacts</font>"));
            }
        }
        Utility.changeStatusBarColor(getActivity());

        db = DBHandler.getInstance(getActivity());
        ListView listView;
        List<Contact> contacts = db.getAllContacts();
        ArrayList<List <String>> allData = new ArrayList<>();

        for (Contact cont : contacts) {
            ArrayList<String> elem = new ArrayList<>();
            elem.add(cont.getName());
            elem.add(cont.getPhone());
            allData.add(elem);
        }

        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new CustomAdapter(getActivity(), allData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textName = (TextView) view.findViewById(R.id.row_name);
                String name = textName.getText().toString();

                Contact contact = db.getContactByName(name);

                mCallback.passData(contact);
            }
        });
        checkPermissions();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DataPassListener");
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkStoragePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int checkSendSmsPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.SEND_SMS);
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE);
            if (checkStoragePermission + checkSendSmsPermission + checkCallPhonePermission
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.SEND_SMS) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.CALL_PHONE)) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ContactCreationActivity.class);
        startActivity(intent);
    }
}
