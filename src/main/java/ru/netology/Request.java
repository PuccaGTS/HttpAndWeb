package ru.netology;

import java.util.Map;

public class Request {
    // read only request line for simplicity
    // must be in form GET /path HTTP/1.1
    private String requestMethod;
    private String path;
    private String httpVersion;
    private StringBuilder titles = new StringBuilder("");
    private StringBuilder body = new StringBuilder("");

    public Request(String requestMethod, String path, String httpVersion) {
        this.requestMethod = requestMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public StringBuilder getTitles() {
        return titles;
    }

    public StringBuilder getBody() {
        return body;
    }

    public void appendTitles(String titles) {
        this.titles.append(titles);
    }

    public void appendBody(String body) {
        this.body.append(body);
    }

}
