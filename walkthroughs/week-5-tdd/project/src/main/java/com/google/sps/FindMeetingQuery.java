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

package com.google.sps;

import java.util.Collection;
import java.util.*;
import java.util.stream.Stream; 
import java.util.stream.Collectors;

/*
  Finds times for a meeting to take place
*/
public final class FindMeetingQuery {

  // The end of the day, i.e. 1440 minutes. 
  private final int LATEST_TIME = TimeRange.WHOLE_DAY.end();

  /* 
    This finds all times where event could take place.
    @param events Takes a collection of events
    @param request Takes a MeetingRequest
    @return Collection<TimeRange> This returns the times where the meeting can take place.
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Set<String> desiredGuests = new HashSet<>();
    Set<String> possibleGuests = new HashSet<>();
    desiredGuests.addAll(request.getAttendees());
    possibleGuests.addAll(request.getOptionalAttendees());
    int duration = (int) request.getDuration();

    if (duration > TimeRange.WHOLE_DAY.duration()){
      return new ArrayList<>();
    }
    if (desiredGuests.size() == 0){
      if (possibleGuests.size() == 0){
        ArrayList<TimeRange> freeDay = new ArrayList<>();
        freeDay.add(TimeRange.WHOLE_DAY);
        return freeDay;
      } else {
        return checkOptionalAttendance(possibleGuests, duration, events, null);
      }
    }
    int start = 0;

    // Find all the times when our desired attendees are busy
    ArrayList<ArrayList<TimeRange>> busyTimes = findBusyTimes(desiredGuests, events);
    
    // Use our busy times list to find all times when our guests are NOT busy
    ArrayList<ArrayList<TimeRange>> allTimes = new ArrayList<>();
    for (ArrayList<TimeRange> guestSchedule : busyTimes){
      allTimes.add(findAvailableTimes(guestSchedule, duration));
    }
    ArrayList<TimeRange> potentialTimes = findIntersectionBetweenLists(allTimes, duration);
    if (possibleGuests.size() > 0){
        return checkOptionalAttendance(possibleGuests, duration, events, potentialTimes);
    } else {
        return potentialTimes;
    }
  }

  /* 
   Find all the times when our desired attendees are busy
   @param desiredGuests Takes a set of guests
   @param events Takes a collection of events
   @return ArrayList<ArrayList<TimeRange>> Returns a list filled with each guest's schedule
  */ 
  private ArrayList<ArrayList<TimeRange>> findBusyTimes(Set<String> desiredGuests, Collection<Event> events){
    ArrayList<ArrayList<TimeRange>> busyTimes = new ArrayList<>();
    for (String guest : desiredGuests){
      ArrayList<TimeRange> guestBusy = new ArrayList<>();
      for (Event e : events){
        // See if they are attending the event
        if (e.getAttendees().contains(guest)){
          // Get the time of the event so we can mark as busy
          guestBusy.add(e.getWhen());
        }
      }
      busyTimes.add(guestBusy);
    }

    return busyTimes;
  }
    

  /* 
   Given a guest's schedule, finds all open times during schedule
   where a meeting could take place.
   @param guestSchedule Takes a list of when guests are busy
   @param duration The length of the desired meeting
   @return ArrayList<TimeRange> Returns a list containing the TimeRanges where the meeting could occur
  */
  private ArrayList<TimeRange> findAvailableTimes(ArrayList<TimeRange> guestSchedule, int duration){
    ArrayList<TimeRange> available = new ArrayList<>();
    // If their schedule is empty, they are free the entire day
    if (guestSchedule.size() == 0){
      available.add(TimeRange.WHOLE_DAY);
    } else {
      Collections.sort(guestSchedule, TimeRange.ORDER_BY_START);
      int st = 0;
      TimeRange t = null;
      // Find available time ranges between each event in their schedule
      for (int i = 0; i < guestSchedule.size(); i++){
        t = guestSchedule.get(i);
        if (st < t.start() && t.start() - st >= duration){
          TimeRange possible = TimeRange.fromStartEnd(st, t.start(), false);
          boolean modified = false;
          // Accounts for double-booked schedules. 
          for (TimeRange x : guestSchedule){
            if (x.overlaps(possible)){
              modified = true;
              x = findIntersectionBetweenRanges(x, possible, duration);
            }
          }
          if (!modified){
            available.add(possible);
          }
        } else {
          st = t.end();
        }
      }
      // Checks the end of the day for availability
      if (t != null && t.end() < LATEST_TIME && LATEST_TIME - t.end() >= duration){
        available.add(TimeRange.fromStartEnd(t.end(), LATEST_TIME, false));
      }
    }
    return available;
  }


  /* Finds the shared open times between multiple lists
    @param allTimes Takes a list of each guest's schedule
    @param duration The duration of the meeting
    @return Returns the times where all guests are available. 
  */
  private ArrayList<TimeRange> findIntersectionBetweenLists(ArrayList<ArrayList<TimeRange>> allTimes, int duration){
    ArrayList<TimeRange> intersection = new ArrayList<>();

    // If there is only 1 guest, return when they are available.
    if (allTimes.size() == 1){
      return allTimes.get(0);
    }

    // Check each guest with the rest of the guestlist's schedule
    for (int i = 0; i < allTimes.size() - 1; i++){
      if (allTimes.get(i).size() > 0 && allTimes.get(i+1).size() > 0){
        // Finds intersection between two TimeRanges. 
        for (TimeRange t : allTimes.get(i)){
          for (TimeRange c : allTimes.get(i + 1)){
            if (t.overlaps(c)){
              TimeRange intersect = findIntersectionBetweenRanges(t, c, duration);
              if (intersect != null){
                intersection.add(intersect);
              }
            }
          }
        }
      } else {
        // If any of the guests is busy for the entire day, just return an empty list
        return new ArrayList<>();
      }
    } 
    return intersection;
  }


  /* 
   Finds the shared time between two TimeRanges
   @param a The first TimeRange
   @param b The second TimeRange
   @return TimeRange Returns where the two TimeRanges overlap, or null if they do not.
  */
  private TimeRange findIntersectionBetweenRanges(TimeRange a, TimeRange b, int duration){
    int start = Math.max(a.start(), b.start()); 
    int end = Math.min(a.end(), b.end());
    if (end - start >= duration) {
      return TimeRange.fromStartEnd(start, end, false);
    } 
    return null;
  }

  private ArrayList<TimeRange> checkOptionalAttendance(Set<String> guests, int duration,
                            Collection<Event> events, ArrayList<TimeRange> allTimes){
    ArrayList<ArrayList<TimeRange>> busy = findBusyTimes(guests, events);
    ArrayList<ArrayList<TimeRange>> optionalAvailability = new ArrayList<>();
    if (allTimes != null){
      optionalAvailability.add(allTimes);
    }
    for (ArrayList<TimeRange> guestSchedule : busy){
      optionalAvailability.add(findAvailableTimes(guestSchedule, duration));
    }

    ArrayList<TimeRange> inclusion = findIntersectionBetweenLists(optionalAvailability, duration);
    if (inclusion.size() > 0){
      return inclusion;
    } else if (allTimes != null){
      return allTimes;
    } else {
      return new ArrayList<>();
    }
  }
}