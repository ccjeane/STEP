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
    Collection<TimeRange> times = new ArrayList<>();
    Set<String> desiredGuests = new HashSet<>();
    desiredGuests.addAll(request.getAttendees());
    int duration = (int) request.getDuration();
    int time = 0;
    int latestTime = TimeRange.getTimeInMinutes(23,59) - duration;
    while (time <= latestTime){
        for (Event e: events){
            if (e.getWhen().contains(time)){
                Set<String> busy = e.getAttendees();
                
                // Get the intersection between meeting request 
                Set<String> result = busy.stream()
                    .distinct()
                    .filter(desiredGuests::contains)
                    .collect(Collectors.toSet());

                // If there is NOT an intersection, 
                if (result.size() == 0){
                    times.add(TimeRange.fromStartDuration(time, duration));
                }
            }
        }
        time++;
    }

    Collection<TimeRange> simplified = new ArrayList<>();
    if (times.size() > 0){
        int start = times.get(0).start();
        int end = times.get(0).end();
        for (int i = 1; i < times.size(); i++){
            if (times.get(i).start < end && times.get(i).end() < end){
                end = times.get(i).end();
            } else {
                simplified.add(TimeRange.fromStartEnd(start, end, true));
            }
        }
    }

    return simplified;
  }
}
