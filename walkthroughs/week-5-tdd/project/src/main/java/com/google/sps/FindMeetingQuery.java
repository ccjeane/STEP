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
    int start = 0;

    ArrayList<ArrayList<TimeRange>> allTimes = new ArrayList<>();
    for (String guest : desiredGuests){
        ArrayList<TimeRange> guestNotBusy = new ArrayList<>();
        for (Event e : events){
            // See if they are attending the event
            if (e.getAttendees().contains(guest)){
                // Get the time of the event so we can mark as busy until the end
                TimeRange busy = e.getWhen();
                
                if (start < busy.start() && (busy.start() - start) >= duration){
                    guestNotBusy.add(TimeRange.fromStartEnd(start, busy.start(), false));
                } 
                // Update their availability to begin after the current event.
                start = busy.end();
            }
        }
        if (latestTime - start > duration){
            guestNotBusy.add(TimeRange.fromStartEnd(start, latestTime, false));
        }

        allTimes.add(guestNotBusy);
    }

    Collection<TimeRange> intersect = new ArrayList<>();
    if (allTimes.size() >= 1){
        for (TimeRange first : allTimes.get(0)){
            intersect.add(first);
        }
    }

    for (int i = 1; i < allTimes.size(); i++){
        if (allTimes.get(i).size() > 0){
            // Find the intersection between two TimeRanges. If there is no intersection, remove the old TR as it isn't applicable.
            for (TimeRange t : intersect){
                for (TimeRange c : allTimes.get(i)){
                }
            }
        } else {
            // If any individual guest is busy for the entire day, just return an empty list
            return new ArrayList<>();
        }
    }

    return intersect;


    /*
    while (time <= latestTime){
        for (Event e: events){
            if (e.getWhen().contains(time)){
                Set<String> busy = e.getAttendees();
                
                // Get the intersection between meeting request 
                Set<String> result = busy.stream()
                    .distinct()
                    .filter(desiredGuests::contains)
                    .collect(Collectors.toSet());

                // If there is an intersection, end the current time range.
                if (result.size() >= 1 && (time - start) >= duration){
                    times.add(TimeRange.fromStartDuration(start, time));
                    start = e.getWhen().end(); // Start time over after this event finishes
                    time = e.getWhen().end();
                } else {
                    time ++;
                }
            } else {
                time ++;
            }
        }
        time++;
    }
    */

    /*
    Collection<TimeRange> simplified = new ArrayList<>();
    Iterator<TimeRange> it = times.iterator();
    if (times.size() > 0){
        TimeRange tr = it.next();
        int start = tr.start();
        int end = tr.end();
        while(it.hasNext()){
            tr = it.next();
            if (start == -1){
                start = tr.start();
            }
            if (tr.start() < end && tr.end() < end){
                end = tr.end();
            } else {
                simplified.add(TimeRange.fromStartEnd(start, end, true));
                start = -1;
            }
        }
    }
    */


    //return times;
  }
}
