package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    // read only request line for simplicity
    // must be in form GET /path HTTP/1.1
    private String requestMethod;
    private String fullPath;
    private String path;
    private String httpVersion;
    private String titles;
    private String body;
    private String query;
    private Map<String, String> mapParams;

    public Request(String requestMethod, String fullPath, String httpVersion, String titles, String body) {
        this.requestMethod = requestMethod;
        this.fullPath = fullPath;
        this.httpVersion = httpVersion;
        this.titles = titles;
        this.body = body;

        if(fullPath.contains("?")){
            path = fullPath.substring(0, fullPath.indexOf("?"));
            query = fullPath.substring(fullPath.indexOf("?")+1);
            mapParams = URLEncodedUtils.parse(this.query, StandardCharsets.UTF_8).stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
        } else {
            path = fullPath;
        }
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }


    public String getQueryParam(String name){
        return mapParams.get(name);
    }

    public String getTitles() {
        return titles;
    }

    public Map<String,String> getQueryParams(){
        return mapParams;
    }

    public static void showInfo(Request request){
        System.out.println("----------------------------------------------");
        System.out.println("МЕТОД");
        System.out.println(request.getRequestMethod());
        System.out.println("----------------------------------------------");
        System.out.println("ПОЛНЫЙ ПУТЬ С ПАРАМЕТРАМИ");
        System.out.println(request.getFullPath());
        System.out.println("----------------------------------------------");
        System.out.println("ПУТЬ");
        System.out.println(request.getPath());
        System.out.println("----------------------------------------------");
        System.out.println("ВЕРСИЯ ПРОТОКОЛА");
        System.out.println(request.getHttpVersion());
        System.out.println("----------------------------------------------");
        System.out.println("ЗАГОЛОВКИ");
        System.out.println(request.getTitles());
        System.out.println("----------------------------------------------");
        System.out.println("ПАРАМЕТРЫ");
        for (Map.Entry entry : request.getQueryParams().entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }
}
