package ru.netology;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();

    server.addHandler("GET", "/events.html", (request, responseStream) -> {
      Request.showInfo(request);
      try {
        Server.successOk200(request, responseStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    server.listen(9999);
  }
}


