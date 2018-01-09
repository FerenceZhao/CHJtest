package com.example.denic.chjtest;

/**
 * Created by Denic on 2018/1/9.
 */

public class spinner_class {
    private String value = "";
    private String text = "";

    public spinner_class() {
        value = "";
        text = "";
    }

    public spinner_class(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
    public String getValue() {
        return value;
    }
    public String getText() {
        return text;
    }
}
