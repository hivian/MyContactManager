package com.example.hivian.ft_hangouts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactEdition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edition);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            TextView textView1 = (TextView) findViewById(R.id.edit_name);
            TextView textView2 = (TextView) findViewById(R.id.edit_lastName);
            TextView textView3 = (TextView) findViewById(R.id.edit_phone);
            TextView textView4 = (TextView) findViewById(R.id.edit_email);
            TextView textView5 = (TextView) findViewById(R.id.edit_address);

            textView1.setText(extras.getString("name"));
            textView2.setText(extras.getString("lastName"));
            textView3.setText(extras.getString("phone"));
            textView4.setText(extras.getString("email"));
            textView5.setText(extras.getString("address"));
        }
    }
}
