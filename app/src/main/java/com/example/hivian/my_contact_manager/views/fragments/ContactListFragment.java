package com.example.hivian.my_contact_manager.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.recyclers.ContactListRecycler;
import com.example.hivian.my_contact_manager.recyclers.ContactData;
import com.example.hivian.my_contact_manager.recyclers.RecyclerItemClickListener;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.utilities.BitmapUtility;
import com.example.hivian.my_contact_manager.views.activities.ContactCreationActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hivian on 9/4/17.
 */

public class ContactListFragment extends Fragment implements View.OnClickListener {

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public static final int RESULT_REQUEST_CODE = 111;
    private List<ContactData> allData;
    private RecyclerView recyclerView;
    private DBHandler db;
    private DataPassListener mCallback;
    LinearLayout rowList;

    public interface DataPassListener{
        void passData(Contact contact);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        setHasOptionsMenu(true);
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Contacts");
            ab.setDisplayHomeAsUpEnabled(false);
        }

        db = DBHandler.getInstance(getActivity());
        List<Contact> contacts = db.getAllContacts();
        allData = new ArrayList<>();
        Bitmap imageBm = null;

        for (Contact cont : contacts) {
            if (cont.getImage() != null) {
                imageBm = BitmapUtility.getImage(cont.getImage());
            } else {
                imageBm = null;
            }
            allData.add(new ContactData(cont.getName(), cont.getPhone(), imageBm));
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ContactListRecycler(allData));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String name = allData.get(position).getName();
                        Contact contact = db.getContactByName(name);
                        mCallback.passData(contact);
                        rowList = (LinearLayout) view.findViewById(R.id.row_list);
                        rowList.setSelected(true);
                    }
                })
        );

        checkPermissions();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //allData =

            // deal with the item yourself

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
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }
}
