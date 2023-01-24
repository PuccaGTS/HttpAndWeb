package ru.netology;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();
    // код инициализации сервера (из вашего предыдущего ДЗ)
    //"/classic.html?value1=val&value2=val2&image=img.png&Значение1=6578"
    // добавление handler'ов (обработчиков)

    server.addHandler("POST", "/events.html", (request, responseStream) -> {
      Request.showInfo(request);
      try {
        Server.successOk200(request, responseStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

//    server.addHandler("GET", "/asdfasdfdsafdsa", (request, responseStream) -> {
//      Server.notFoundError404(responseStream);
//    });
//    server.addHandler("POST", "/messages", (request, responseStream) -> {
//      // TODO: handlers code
//    });

    server.listen(9999);
  }
}


