package com.highcapable.yukihookapi.demo_app.test;

public class Main extends SuperMain {

    private final String content;

    public Main() {
        super("");
        content = "";
    }

    public Main(String content) {
        super(content);
        this.content = content;
    }

    public String getTestResultFirst() {
        return "The world is beautiful";
    }

    public String getTestResultFirst(String string) {
        return string;
    }

    public String getTestResultLast() {
        return "The world is fantastic";
    }

    public final String getTestResultLast(String string) {
        return string;
    }

    public String getContent() {
        return content;
    }
}