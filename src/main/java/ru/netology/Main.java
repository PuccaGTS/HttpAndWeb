package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();
    // код инициализации сервера (из вашего предыдущего ДЗ)
    //"/classic.html?value1=val&value2=val2&image=img.png&Значение1=6578"
    // добавление handler'ов (обработчиков)
    server.addHandler("GET", "/events.html", (request, responseStream) -> {
      Request.showInfo(request);
      try {
        Server.classicPathWithTime(request, responseStream);
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


