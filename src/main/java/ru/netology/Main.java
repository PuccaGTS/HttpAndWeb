package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();
    // код инициализации сервера (из вашего предыдущего ДЗ)

    // добавление handler'ов (обработчиков)
    server.addHandler("GET", "/events.html", (request, responseStream) -> {
      // TODO: handlers code

    });
    server.addHandler("GET", "/events.js", (request, responseStream) -> {
      // TODO: handlers code

    });
    server.addHandler("POST", "/messages", (request, responseStream) -> {
      // TODO: handlers code
    });

    server.listen(9999);
  }
}


