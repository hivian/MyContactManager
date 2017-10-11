package com.example.hivian.my_contact_manager.recyclers.contact;

import android.graphics.Bitmap;

/**
 * Created by hivian on 10/10/17.
 */

public class ContactData {

    private String _name;
    private String _phone;
    private Bitmap _image;

    public ContactData(String name, String phone, Bitmap image) {
        this._name = name;
        this._phone = phone;
        this._image = image;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getPhone() {
        return _phone;
    }

    public void setPhone(String phone) {
        this._phone = phone;
    }

    public Bitmap getImage() {
        return _image;
    }

    public void setImage(Bitmap image) {
        this._image = image;
    }

}
