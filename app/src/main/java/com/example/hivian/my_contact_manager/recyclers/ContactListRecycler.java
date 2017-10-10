package com.example.hivian.my_contact_manager.recyclers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.hivian.my_contact_manager.R;

import java.util.List;

/**
 * Created by hivian on 1/24/17.
 */

public class ContactListRecycler extends RecyclerView.Adapter<ContactViewHolder> implements View.OnClickListener {

    private List<ContactData> data;

    public ContactListRecycler(List<ContactData> data) {
        this.data = data;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactData contactData = data.get(position);
        holder.bind(contactData);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View view) {

    }
}
