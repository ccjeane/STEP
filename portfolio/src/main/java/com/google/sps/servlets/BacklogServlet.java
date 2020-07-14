package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;
import java.util.*;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns archived content.*/
@WebServlet("/archive")
public class BacklogServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Backlog").addSort("date", SortDirection.DESCENDING);
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Add list of emails allowed to access this secure site
    ArrayList<String> allowedEmails = new ArrayList<String>(){
        {
            add("cjcoulibaly@gmail.com");
            add("claudiacoulibaly18@gmail.com");
            add("ccjeane@google.com");
        }
    };


    if (userService.isUserLoggedIn()) {
        if (allowedEmails.contains(userService.getCurrentUser().getEmail())){
            for (Entity entity : results.asIterable()) {
                String message = (String) entity.getProperty("message");
                Date timestamp = (Date) entity.getProperty("date");
                String user = (String) entity.getProperty("email");
                response.getWriter().println("<p>" + message + " - " + user + " - " + "</p>");
            }
        } else {
            response.getWriter().println("<p>You are not authorized to access this site!</p>");
        }
    } else {
        String urlToRedirectToAfterUserLogsIn = "/archive";
        String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
        response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }
}