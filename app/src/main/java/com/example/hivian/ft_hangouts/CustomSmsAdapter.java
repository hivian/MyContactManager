package com.example.hivian.ft_hangouts;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hivian on 1/31/17.
 */

public class CustomSmsAdapter extends BaseAdapter {

    Context context;
    List<List<String>> data;
    private static LayoutInflater inflater = null;

    public CustomSmsAdapter(Context context, List<List<String>> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(SmsContent object, int color) {
        List <String> elem = new ArrayList<>();
        elem.add(object.getHeader());
        elem.add(object.getContent()        );
        data.add(elem);
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
            vi = inflater.inflate(R.layout.row_sms,  null);
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.smsHeader_container);
        TextView text1 = (TextView) vi.findViewById(R.id.sms_header);
        TextView text2 = (TextView) vi.findViewById(R.id.sms_content);
        text1.setText(data.get(position).get(0));
        text2.setText(data.get(position).get(1));
        Log.d("ICI1", text1.getText().toString().substring(0, 5));
        Log.d("ICI2", parent.getResources().getString(R.string.header_sending_sms));
        if (text1.getText().toString().equals(parent.getResources().getString(R.string.header_sending_sms))) {
            layout.setBackgroundColor(Color.WHITE);
            layout.setGravity(Gravity.END);
            text1.setGravity(Gravity.END);
            text2.setGravity(Gravity.END);
        }
        else {
            layout.setBackgroundColor(Color.BLUE);
            layout.setGravity(Gravity.START);
            text1.setGravity(Gravity.START);
            text2.setGravity(Gravity.START);
        }

        return vi;
    }
}
