package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();
    // код инициализации сервера (из вашего предыдущего ДЗ)

    // добавление handler'ов (обработчиков)
    server.addHandler("GET", "/classic.html", (request, responseStream) -> {
      try {
        Server.classicPathWithTime(request, responseStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    server.addHandler("GET", "/asdfasdfdsafdsa", (request, responseStream) -> {
      Server.notFoundError404(responseStream);
    });
//    server.addHandler("POST", "/messages", (request, responseStream) -> {
//      // TODO: handlers code
//    });

    server.listen(9999);
  }
}


