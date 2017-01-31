package com.example.hivian.ft_hangouts;

import android.content.Context;
import android.graphics.Color;
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
    int contentColor;

    public CustomSmsAdapter(Context context, List<List<String>> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.contentColor = Color.WHITE;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(SmsContent object, int color) {
        List <String> elem = new ArrayList<>();
        elem.add(object.getHeader());
        elem.add(object.getContent());
        contentColor = color;
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
            vi = inflater.inflate(R.layout.row_sms, null);
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.smsHeader_container);
        TextView text1 = (TextView) vi.findViewById(R.id.sms_header);
        TextView text2 = (TextView) vi.findViewById(R.id.sms_content);
        text1.setText(data.get(position).get(0));
        text2.setText(data.get(position).get(1));
        layout.setBackgroundColor(contentColor);

        return vi;
    }
}
