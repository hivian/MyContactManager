package com.example.hivian.ft_hangouts;

import android.graphics.Bitmap;

/**
 * Created by hivian on 1/24/17.
 */

public class Contact {

    private Integer _id;
    private byte[] _image;
    private String _name;
    private String _lastName;
    private String _phone;
    private String _email;
    private String _address;

    public Contact() {

    }

    public Contact(byte[] image, String name, String lastName, String phone, String email, String address) {
        this._image = image;
        this._name = name;
        this._lastName = lastName;
        this._phone = phone;
        this._email = email;
        this._address = address;
    }

    public void setId(Integer id) {
        this._id = id;
    }

    public void setImage(byte[] image) {
        this._image = image;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void setLastName(String lastName) {
        this._lastName = lastName;
    }

    public void setPhone(String phone) {
        this._phone = phone;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public void setAddress(String address) {
        this._address = address;
    }

    public Integer getId() {
        return _id;
    }

    public byte[] getImage() {
        return _image;
    }

    public String getName() {
        return _name;
    }

    public String getLastName() {
        return _lastName;
    }

    public String getPhone() {
        return _phone;
    }

    public String getEmail() {
        return _email;
    }

    public String getAddress() {
        return _address;
    }

}
