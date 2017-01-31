package com.example.hivian.ft_hangouts;

import java.io.Serializable;

/**
 * Created by hivian on 1/30/17.
 */

public class SmsContent implements Serializable {

    private String _header;
    private String _content;
    private Integer _contactId;

    SmsContent(String header, String content, Integer contactId) {
        this._header = header;
        this._content = content;
        this._contactId = contactId;
    }

    public String getHeader() {
        return _header;
    }

    public String getContent() {
        return _content;
    }

    public Integer getContactId() {
        return _contactId;
    }

    void setHeader(String header) {
        this._header = header;
    }

    void setContent(String content) {
        this._content = content;
    }

    void setContactId(Integer contactId) {
        this._contactId = contactId;
    }
}
