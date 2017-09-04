package com.example.hivian.my_contact_manager.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hivian.my_contact_manager.utilities.BitmapUtility;
import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.db.DBHandler;

import java.util.List;

/**
 * Created by hivian on 1/24/17.
 */

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<List<String>> data;
    private static LayoutInflater inflater = null;
    DBHandler db;

    public CustomAdapter(Context context, List<List<String>> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);

        db = DBHandler.getInstance(parent.getContext());

        ImageView image = (ImageView) vi.findViewById(R.id.row_image);
        TextView name = (TextView) vi.findViewById(R.id.row_name);
        TextView phone = (TextView) vi.findViewById(R.id.row_phone);
        Contact contact = db.getContactByName(data.get(position).get(0));
        if (contact.getImage() != null) {
            Bitmap imageBm = BitmapUtility.getImage(contact.getImage());
            Resources res = vi.getResources();
            image.setImageDrawable(BitmapUtility.setBitmapCircular(res, imageBm));
        }
        name.setText(data.get(position).get(0));
        phone.setText(data.get(position).get(1));

        return vi;
    }

}
