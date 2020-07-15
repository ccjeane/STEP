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

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Set<String> desiredGuests = new HashSet<>();
    desiredGuests.addAll(request.getAttendees());
    int duration = (int) request.getDuration();
    int latestTime = TimeRange.WHOLE_DAY.end();

    if (duration > latestTime){
        return new ArrayList<>();
    }
    if (desiredGuests.size() == 0){
        ArrayList<TimeRange> freeDay = new ArrayList<>();
        freeDay.add(TimeRange.WHOLE_DAY);
        return freeDay;
    }
    int start = 0;

    // Find all the times when our desired attendees are busy
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
    
    // Use our busy times list to find all times when our guests are NOT busy
    ArrayList<ArrayList<TimeRange>> allTimes = new ArrayList<>();
    for (ArrayList<TimeRange> guestSchedule : busyTimes){
        allTimes.add(findAvailability(guestSchedule, duration));
    }
    return findIntersectionBetweenLists(allTimes, duration);
  }

  // Given a guest's schedule and a meeting duration, finds all open times during schedule
  // where a meeting could take place.
  public ArrayList<TimeRange> findAvailability(ArrayList<TimeRange> guestSchedule, int duration){
    ArrayList<TimeRange> available = new ArrayList<>();
    int latestTime = TimeRange.WHOLE_DAY.end();
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
        if (t != null && t.end() < latestTime && latestTime - t.end() >= duration){
            available.add(TimeRange.fromStartEnd(t.end(), latestTime, false));
        }
    }
        return available;
  }


  // Finds the shared open times between multiple lists
  public List<TimeRange> findIntersectionBetweenLists(ArrayList<ArrayList<TimeRange>> allTimes, int duration){
    List<TimeRange> intersection = new ArrayList<>();

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
                        intersection.add(findIntersectionBetweenRanges(t, c, duration));
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


  // Finds the shared time between two TimeRanges
  // If there is no intersection, returns null
  public TimeRange findIntersectionBetweenRanges(TimeRange a, TimeRange b, int duration){
      int start = Math.max(a.start(), b.start()); 
      int end = Math.min(a.end(), b.end());
      if (end - start >= duration) {
          return TimeRange.fromStartEnd(start, end, false);
      } 
      return null;
  }
}