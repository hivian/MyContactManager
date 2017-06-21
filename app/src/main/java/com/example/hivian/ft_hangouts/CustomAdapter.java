package com.example.hivian.ft_hangouts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        db = new DBHandler(parent.getContext());
        ImageView image = (ImageView) vi.findViewById(R.id.row_image);
        TextView name = (TextView) vi.findViewById(R.id.row_name);
        TextView phone = (TextView) vi.findViewById(R.id.row_phone);
        Contact contact = db.getContactByName(data.get(position).get(0));
        if (contact.getImage() != null) {
            Bitmap imageBm = DbBitmapUtility.getImage(contact.getImage());
            Resources res = vi.getResources();
            image.setImageDrawable(DbBitmapUtility.setBitmapCircular(res, imageBm));
        }
        name.setText(data.get(position).get(0));
        phone.setText(data.get(position).get(1));
        db.close();

        return vi;
    }

}
