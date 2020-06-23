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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Movie: The Lion King', 'Color: Burgundy', 'Show: How I Met Your Mother / Criminal Minds', 'Food: Spaghetti', 'Drink: Iced Coffee',
        'Time of Day: Morning', 'Season: Late Spring', 'Place visited: Italy or Hawaii', 'Music Genre: Hip hop',
        'Rapper: Drake', 'Scenery: Beach', 'Activity: Photography', 'Sport with a ball: Golf', 'Sport without a ball: Cheerleading/Gymnastics',
        'Shoes: Nike Air Force One\'s (Look at the pics, couldn\'t you tell?)'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
