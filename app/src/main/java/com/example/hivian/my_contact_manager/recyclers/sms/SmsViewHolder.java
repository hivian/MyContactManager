package com.example.hivian.my_contact_manager.recyclers.sms;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.models.Sms;

/**
 * Created by hivian on 10/11/17.
 */

public class SmsViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private TextView header;
    private TextView content;
    private LinearLayout sms_container;
    private GradientDrawable gradientDrawable;
    private LinearLayout.LayoutParams container_params;
    private TableRow.LayoutParams text_params;

    SmsViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        header = (TextView) itemView.findViewById(R.id.row_sms_header);
        content = (TextView) itemView.findViewById(R.id.row_sms_content);

        sms_container = (LinearLayout) itemView.findViewById(R.id.sms_container);
        container_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        text_params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

    }

    void bind(SmsData data) {
        content.setText(data.getContent());
        if (data.getType().equals(Sms.SENT)) {
            setSmsLayout(R.color.colorOrange, R.color.colorDarkBlue, Gravity.END);
            header.setText(context.getResources().getString(R.string.header_sending_sms)
                    + " " + data.getHeader());
        } else {
            setSmsLayout(R.color.colorLightBlue, R.color.colorRed, Gravity.START);
            header.setText(context.getResources().getString(R.string.header_received_sms)
                    + " " + data.getHeader());
        }
    }

    private void setSmsLayout(int bg_color, int text_color, int gravity) {
        container_params.gravity = gravity;
        text_params.gravity = gravity;

        sms_container.setBackgroundResource(R.drawable.corner_sms);
        gradientDrawable = (GradientDrawable) sms_container.getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(context, bg_color));
        sms_container.setBackground(gradientDrawable);
        sms_container.setGravity(gravity);

        sms_container.setLayoutParams(container_params);
        header.setLayoutParams(text_params);
        content.setLayoutParams(text_params);
        content.setTextColor(ContextCompat.getColor(context, text_color));
    }

}
