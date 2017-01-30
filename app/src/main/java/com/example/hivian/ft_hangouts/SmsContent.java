package com.example.hivian.ft_hangouts;

import java.io.Serializable;

/**
 * Created by hivian on 1/30/17.
 */

public class SmsContent implements Serializable {

    private String header;
    private String content;

    SmsContent(String header, String content) {
        this.header = header;
        this.content = content;
    }

    private String getHeader() {
        return header;
    }

    private String getContent() {
        return content;
    }

    void setHeader(String header) {
        this.header = header;
    }

    void setContent(String content) {
        this.content = content;
    }
}
