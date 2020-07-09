
package com.google.sps.data;
import java.util.*;

/** An individual comment*/
public final class Comment{

  private final long id;
  private final String comment;
  private final Date timestamp;
  private final String user;

  public Comment(long id, String comment, Date timestamp, String user) {
    this.id = id;
    this.comment = comment;
    this.timestamp = timestamp;
    this.user = user;
  }
}