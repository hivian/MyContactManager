package com.example.hivian.ft_hangouts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
        GradientDrawable gradientDrawable;
        TextView text1 = (TextView) vi.findViewById(R.id.sms_header);
        TextView text2 = (TextView) vi.findViewById(R.id.sms_content);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

        text2.setText(data.get(position).get(1));
        if (Integer.valueOf(data.get(position).get(2)) == SmsContent.SENT) {
            params1.gravity = Gravity.END;
            params2.gravity = Gravity.END;
            layout.setLayoutParams(params1);
            layout.setBackgroundResource(R.drawable.corner_sms);
            gradientDrawable = (GradientDrawable)layout.getBackground();
            gradientDrawable.setColor(parent.getResources().getColor(R.color.colorGreen));
            layout.setBackground(gradientDrawable);
            layout.setGravity(Gravity.END);
            text1.setLayoutParams(params2);
            text2.setLayoutParams(params2);
            text1.setText(parent.getResources().getString(R.string.header_sending_sms)
                    + " " + data.get(position).get(0));
        } else {
            params1.gravity = Gravity.START;
            params2.gravity = Gravity.START;
            layout.setLayoutParams(params1);
            layout.setBackgroundResource(R.drawable.corner_sms);
            gradientDrawable = (GradientDrawable)layout.getBackground();
            gradientDrawable.setColor(parent.getResources().getColor(R.color.colorOrange));
            layout.setBackground(gradientDrawable);
            text1.setLayoutParams(params2);
            text2.setLayoutParams(params2);
            text1.setText(parent.getResources().getString(R.string.header_received_sms)
                    + " " + data.get(position).get(0));
        }

        return vi;
    }
}
