package com.highcapable.yukihookapi.demo_app.test;

public class SuperMain {

    private final String content;

    public SuperMain(String content) {
        this.content = content;
    }

    public String getSuperString() {
        return "The sea is blue";
    }

    public String getString() {
        return getContent();
    }

    public String getContent() {
        return content;
    }
}