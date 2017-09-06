package com.example.hivian.my_contact_manager.views.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.Sms;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.utilities.BitmapUtility;
import com.example.hivian.my_contact_manager.views.activities.ContactEditionActivity;
import com.example.hivian.my_contact_manager.views.activities.ContactSmsActivity;
import com.example.hivian.my_contact_manager.views.activities.MainActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;

/**
 * Created by hivian on 9/4/17.
 */

public class ContactInfoFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private TextView name;
    private TextView phone;
    public FloatingActionMenu actionMenu;
    private DBHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_info, container, false);

        setHasOptionsMenu(true);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Options");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        db = DBHandler.getInstance(getActivity());

        Contact receivedContact = (Contact) getArguments().getSerializable("contact");

        if (receivedContact != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.info_image);
            name = (TextView) view.findViewById(R.id.info_name);
            phone = (TextView) view.findViewById(R.id.info_phone);
            TextView email = (TextView) view.findViewById(R.id.info_email);
            TextView address = (TextView) view.findViewById(R.id.info_address);

            if (receivedContact.getImage() != null) {
                Bitmap imageBm = BitmapUtility.getImage(receivedContact.getImage());
                imageView.setImageBitmap(imageBm);
            }
            name.setText(receivedContact.getName());
            phone.setText(receivedContact.getPhone());
            if (receivedContact.getEmail().equals("")) {
                email.setTypeface(null, Typeface.ITALIC);
                email.setTextColor(Color.GRAY);
                email.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                email.setText(R.string.placeholder_none);
            }
            else
                email.setText(receivedContact.getEmail());
            if (receivedContact.getAddress().equals("")) {
                address.setTypeface(null, Typeface.ITALIC);
                address.setTextColor(Color.GRAY);
                address.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
                address.setText(R.string.placeholder_none);
            }
            else
                address.setText(receivedContact.getAddress());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initInfoMenu(view);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollview_contact_info);
        scrollView.setOnTouchListener(this);

        super.onViewCreated(view, savedInstanceState);
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
        actionMenu.close(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                actionMenu.close(true);
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initInfoMenu(View view) {
        ImageView menuIcon1 = new ImageView(getActivity());
        ImageView menuIcon2 = new ImageView(getActivity());
        ImageView menuIcon3 = new ImageView(getActivity());
        ImageView menuIcon4 = new ImageView(getActivity());

        menuIcon1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message_black_24dp, null));
        menuIcon2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_call_black_24dp, null));
        menuIcon3.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mode_edit_black_24dp, null));
        menuIcon4.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_black_24dp, null));
        menuIcon1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_opacity, null));
        menuIcon1.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white));
        menuIcon2.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white));
        menuIcon3.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white));
        menuIcon4.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white));
        menuIcon1.setPadding(15,15,15,15);
        menuIcon2.setPadding(15,15,15,15);
        menuIcon3.setPadding(15,15,15,15);
        menuIcon4.setPadding(15,15,15,15);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());
        itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(160,160));

        ImageView menuInfo = (ImageView) view.findViewById(R.id.info_menu);
        actionMenu = new FloatingActionMenu.Builder(getActivity())
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
        name = (TextView) getActivity().findViewById(R.id.info_name);
        phone = (TextView) getActivity().findViewById(R.id.info_phone);

        Intent intent = new Intent(getActivity(), ContactSmsActivity.class);

        intent.putExtra("name", name.getText().toString());
        intent.putExtra("phone", phone.getText().toString());
        startActivity(intent);
    }

    private void callContact() {
        phone = (TextView) getActivity().findViewById(R.id.info_phone);
        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + phone.getText().toString()));
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            Toast toast = Toast.makeText(getActivity(), R.string.alert_no_call_perm, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void editContact() {
        name = (TextView) getActivity().findViewById(R.id.info_name);

        Contact contact = db.getContactByName(name.getText().toString());
        Intent intent = new Intent(getActivity(), ContactEditionActivity.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    private void deleteContact() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(getResources().getString(R.string.alert_delete_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.alert_delete_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                TextView textView = (TextView) getActivity().findViewById(R.id.info_name);
                                Contact contact = db.getContactByName(textView.getText().toString());
                                db.deleteContact(contact);
                                List<Sms> allSms = db.getAllSmsFromContact(contact.getId());
                                for (Sms sms : allSms) {
                                    db.deleteSms(sms);
                                }
                                ContactListFragment.getAdapter().notifyDataSetChanged();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.alert_delete_cancel), null)
                .show();
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        buttonPositive.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        buttonNegative.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }
}
