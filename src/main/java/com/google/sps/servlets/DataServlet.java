package com.google.sps.servlets;


import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * {@code DataServlet} is used to provide interaction between front-end part of the application
 * and the storage.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final static Object STORAGE_CONTROLLER = null;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(""));
  }
}
