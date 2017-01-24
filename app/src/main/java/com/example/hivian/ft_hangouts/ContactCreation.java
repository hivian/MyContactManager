package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class ContactCreation extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_creation);

        Button button = (Button)findViewById(R.id.button_save);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + getString(R.string.create_contact)  + "</font>"));

        if (MainActivity.getYellow()) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorYellow)));
            button.setBackgroundColor(getResources().getColor(R.color.colorYellow));
            Log.d("DEBUG", "IS YELLOW");
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Log.d("DEBUG", "IS_BLUE");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    public void browseFolder(View view) {
        Log.d("DEBUG", "BROWSE");
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void saveContact(View view) {
        Log.d("DEBUG", "SAVE FUNCTION");
        EditText name = (EditText)findViewById(R.id.name);
        EditText lastName = (EditText)findViewById(R.id.lastName);
        EditText phone = (EditText)findViewById(R.id.phone);
        EditText email = (EditText)findViewById(R.id.email);
        EditText addr = (EditText)findViewById(R.id.address);

        if (name.getText().toString().trim().length() == 0) {
            Toast toast = Toast.makeText(this, R.string.alert_no_name, Toast.LENGTH_LONG);
            toast.show();
        }
        Log.v("EditText1", name.getText().toString());
        Log.v("EditText2", lastName.getText().toString());
        Log.v("EditText3", phone.getText().toString());
        Log.v("EditText4", email.getText().toString());
        Log.v("EditText5", addr.getText().toString());
    }
}
