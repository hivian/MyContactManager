package com.example.hivian.my_contact_manager.recyclers;

import android.graphics.Bitmap;

/**
 * Created by hivian on 10/10/17.
 */

public class ContactData {

    private String name;
    private String phone;
    private Bitmap image;

    public ContactData(String name, String phone, Bitmap image) {
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
