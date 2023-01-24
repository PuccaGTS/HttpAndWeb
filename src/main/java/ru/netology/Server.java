package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Server {
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

    public static void notFoundError404(BufferedOutputStream out) {
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

    public static void classicPathWithTime(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);

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

    public static void successOk200(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);

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
                String requestMethod = parts[0];
                String fullPath = parts[1];
                String httpVersion = parts[2];
                StringBuilder titles = new StringBuilder("");
                StringBuilder body = new StringBuilder("");

                while (in.ready()){
                    String line = in.readLine();
                    if (line.equals("\r\n")){
                        break;
                    }
                    titles.append(line);
                }

                while (in.ready()){
                    String line = in.readLine();
                    body.append(line);
                }

                Request request = new Request(requestMethod, fullPath, httpVersion, titles.toString(), body.toString());

                if ((!(handlers.containsKey(request.getRequestMethod()))) && (!(handlers.get(request.getRequestMethod()).containsKey(request.getPath())))){
                    notFoundError404(out);
                    break;
                } else {
                    handlers.get(request.getRequestMethod()).get(request.getPath()).handle(request, out);
                }
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
    }
}
