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
function addRandomFact() {
  const facts =
      ['Movie: The Lion King', 'Color: Burgundy', 'Show: How I Met Your Mother / Criminal Minds', 'Food: Spaghetti', 'Drink: Iced Coffee',
        'Time of Day: Morning', 'Season: Late Spring', 'Place visited: Italy or Hawaii', 'Music Genre: Hip hop',
        'Rapper: Drake', 'Scenery: Beach', 'Activity: Photography', 'Sport with a ball: Golf', 'Sport without a ball: Cheerleading/Gymnastics',
        'Shoes: Nike Air Force One\'s'];

  // Pick a random greeting.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Fetches the current state of the game and builds the UI.
 */
function loadComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
    // Build the list of comments
    const historyEl = document.getElementById('history');
    comments.forEach((line) => {
      historyEl.appendChild(createListElement(line));
    });
  });
}

/** Creates an paragraph element containing text. */
function createListElement(text) {
  const pElement = document.createElement("p");
  pElement.innerText = text;
  pElement.className = 'comment';

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(comment);
    // Remove the comment from the DOM.
    pElement.remove();
  });

  pElement.appendChild(deleteButtonElement);
  return pElement;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.Key);
  fetch('/delete-data', {method: 'POST', body: params});
}