
package com.google.sps.data;
import java.util.*;

/** An individual comment*/
public final class Comment{

  private final long id;
  private final String comment;
  private final Date timestamp;

  public Comment(long id, String comment, Date timestamp) {
    this.id = id;
    this.comment = comment;
    this.timestamp = timestamp;
  }
}