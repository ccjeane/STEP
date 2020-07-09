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
    var user = comments.shift();
    comments.forEach((line) => {
        historyEl.appendChild(createListElement(line, user.user));
    });
  });
}

/** Creates an paragraph element containing text. */
function createListElement(text, user) {
  const pElement = document.createElement("p");
  pElement.innerText = text.comment + " - " + text.timestamp;

  // Adds a button to make their comments deletable from public eye
  const deleteButtonElement = document.createElement('button');
  if (text.user == user){
    deleteButtonElement.innerText = 'Archive';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(text);
        // Remove the comment from the DOM.
        pElement.remove();
    });

    pElement.appendChild(deleteButtonElement);
  }
  return pElement;
}


// Delete the comment from the Comments Server 
function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-data', {method: 'POST', body: params});
}

function createMap() {
  // Centered in West Seattle 
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 47.5667, lng: -122.3868}, zoom: 13, mapTypeId: 'satellite'}); 
  var wshs = new google.maps.Marker({position: {lat: 47.5766, lng: -122.3846}, map: map});
  var alki = new google.maps.Marker({position: {lat: 47.5773, lng: -122.4078}, map: map});
  var junction = new google.maps.Marker({position: {lat: 47.5612, lng: -122.3870}, map: map});

  var wshsString = '<div id="content">'+
      '<p style="color:black">This is West Seattle High School. Claudia graduated from here in 2018'+
      'with an unweighted GPA of 3.92.</p>'+
      '<p style="color:black">She was on the Varsity Cheerleading squad, the Varsity Golf Team,'+ 
      'and was student body Vice President.</p>'+
      '</div>';

  var alkiString = '<div id="content">'+
      '<p style="color:black">This is Alki Elementary School. Claudia grew up on Alki beach'+ 
      ' and has always loved being near the water.</p>'+
      '</div>';

  var junctionString = '<div id="content">'+
      '<p style="color:black">This is the Alaska Junction. It is the central point of West Seattle, '+ 
      'home to many local shops & restaurants.</p>'+
      '<p style="color:black">Claudia had her first job at Shelbys Diner here in 2016.</p>'+
      '</div>';

  var infoWindow = new google.maps.InfoWindow({});

  wshs.addListener("click", function() {
    infoWindow.setContent(wshsString);
    infoWindow.open(map, wshs);
  });

  alki.addListener("click", function(){
    infoWindow.setContent(alkiString);
    infoWindow.open(map, alki);
  });

  junction.addListener("click", function(){
    infoWindow.setContent(junctionString);
    infoWindow.open(map, junction);
  });
}