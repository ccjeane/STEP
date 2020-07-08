// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;
import java.util.*;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  // Keeps track of number of desired comments to be shown 
  private int quantity; 
  private boolean set;

  @Override
  public void init(){
      set = false;
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("date", SortDirection.DESCENDING);

    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList<>();
    if (!set) { quantity = 10; }  // Default set at max of 10 comments shown upon loading screen
    int i = 0;
    for (Entity entity : results.asIterable()) {
      // Limits number of comments added to the page
      if (i < quantity){ 
        long id = entity.getKey().getId();
        String message = (String) entity.getProperty("comment");
        Date timestamp = (Date) entity.getProperty("date");
        comments.add(new Comment(id, message, timestamp));
        i++;
      } else {
        break; // Exits for-loop once requested number of comments appear
      }
    }

    response.setContentType("application/json");
    String json = new Gson().toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String newComment = request.getParameter("new-comment");
    String quant = request.getParameter("quantity");

    try {
        quantity = Integer.parseInt(quant);
        set = true;
    } catch (NumberFormatException e) {}

    if (newComment != null && newComment.length() > 0){
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("comment", newComment);
        commentEntity.setProperty("date", new Date());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
    }
    // Redirect back to the same page
    response.sendRedirect("/comment.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
