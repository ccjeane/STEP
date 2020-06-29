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

import java.util.*;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private List<String> people;

  @Override
  public void init() {
    people = new ArrayList<>();
    people.add("Claudia");
    people.add("Kelly");
    people.add("Keenan");
    people.add("Noah");
    people.add("Christy");
    people.add("Brenda");
    people.add("Bob");
    people.add("Isaiah");
    people.add("Alexa");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String greeting = "Hello " + people.get((int) (Math.random() * people.size()));
    
    Gson gson = new Gson();
    String json = gson.toJson(greeting);
    
    response.setContentType("text/html");
    response.getWriter().println(json);
  }
}
