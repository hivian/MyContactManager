package com.example.hivian.my_contact_manager.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.models.Sms;

import java.util.List;

/**
 * Created by hivian on 1/31/17.
 */

public class CustomSmsAdapter extends BaseAdapter {

    private Context context;
    private List<List<String>> data;
    private static LayoutInflater inflater = null;

    public CustomSmsAdapter(Context context, List<List<String>> data) {
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
        if (Integer.valueOf(data.get(position).get(2)).equals(Sms.SENT)) {
            params1.gravity = Gravity.END;
            params2.gravity = Gravity.END;
            layout.setLayoutParams(params1);
            layout.setBackgroundResource(R.drawable.corner_sms);
            gradientDrawable = (GradientDrawable)layout.getBackground();
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorOrange));
            layout.setBackground(gradientDrawable);
            layout.setGravity(Gravity.END);
            text1.setLayoutParams(params2);
            text2.setLayoutParams(params2);
            text2.setTextColor(ContextCompat.getColor(context, R.color.colorDarkBlue));
            text1.setText(parent.getResources().getString(R.string.header_sending_sms)
                    + " " + data.get(position).get(0));
        } else {
            params1.gravity = Gravity.START;
            params2.gravity = Gravity.START;
            layout.setLayoutParams(params1);
            layout.setBackgroundResource(R.drawable.corner_sms);
            gradientDrawable = (GradientDrawable)layout.getBackground();
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorLightBlue));
            layout.setBackground(gradientDrawable);
            text1.setLayoutParams(params2);
            text2.setLayoutParams(params2);
            text2.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            text1.setText(parent.getResources().getString(R.string.header_received_sms)
                    + " " + data.get(position).get(0));
        }

        return vi;
    }
}
