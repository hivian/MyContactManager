package com.example.hivian.ft_hangouts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;

public class ContactCreation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_creation);

        Intent intent = getIntent();

        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.create_contact)  + "</font>"));
    }
}
