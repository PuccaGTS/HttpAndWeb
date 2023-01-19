package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Server {
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final ExecutorService executor;
    private ServerSocket serverSocket;

    private Map<String, Map<String,Handler>> handlers = new ConcurrentHashMap<>();

    public Server() {
            executor = newFixedThreadPool(64);
    }

    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                final var socket = serverSocket.accept();
                executor.submit(()->{
                    this.processingRequests(socket);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void notFoundError404(OutputStream out) {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void classicPathWithTime(Path filePath, OutputStream out, String mimeType) {
        try {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void successOk200(Path filePath, OutputStream out, String mimeType) {
        try {
            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processingRequests(Socket socket) {
        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        try(
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                final var requestLine = in.readLine();
                if (requestLine == null) {
                    break;
                }
                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                    // just close socket
                    break;
                }
                Request request = new Request(parts[0], parts[1], parts[2]);

                while (in.ready()){
                    String line = in.readLine();
                    if (line.equals("\r\n")){
                        break;
                    }
                    request.appendTitles(line);
                }

                while (in.ready()){
                    String line = in.readLine();
                    request.appendBody(line);
                }

                if ((!(handlers.containsKey(request.getRequestMethod()))) || (!(handlers.get(request.getRequestMethod()).containsKey(request.getPath())))){
                    notFoundError404(out);
                    break;
                }
//                final var path = parts[1];
//                if (!validPaths.contains(path)) {
//                    notFoundError404(out);
//                    break;
//                }

                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (request.getPath().equals("/classic.html")) {
                    classicPathWithTime(filePath, out, mimeType);
                    break;
                }
                successOk200(filePath, out, mimeType);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addHandler(String requestMethod, String path, Handler handler) {
        if (handlers.containsKey(requestMethod)){
            handlers.get(requestMethod).put(path,handler);
        } else {
            handlers.put(requestMethod, new ConcurrentHashMap<>());
            handlers.get(requestMethod).put(path, handler);
        }

//        if (handlers.get(requestMethod)!=null){
//            handlers.get(requestMethod).put(path,handler);
//        } else {
//            handlers.put(requestMethod, new HashMap<>());
//            handlers.get(requestMethod).put(path, handler);
//        }
    }
}
