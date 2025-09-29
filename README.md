# Java application assignment: *CarSpotter*
This project was an assignment for our studies as Industrial Engineer in Electronics & ICT, in the second year of our bachelor at GroepT, KULeuven.
The purpose was to learn to manage a project and to create a fully working android application including API's we could find on the internet, without strict guidance from the TA's.

## Made by:
- Wout Lyen ([@woutlyen](https://www.github.com/woutlyen))
- Jonathan Valgaeren ([@ItsAlphie](https://www.github.com/ItsAlphie))

## Inspiration
The original idea for the application was a form of wikipedia, but all about cars. However, we have changed to a more user-interactive application. The main goal of the app to share sightings or 'spots' of cars with others. We have retained the wiki side of the app, where the user is able to look up information about the spotted cars. We also added an event feature for even more user-interaction.
## Functions
### Home page
Upon opening the app the user will find themselves on the home page. Here they can browse through the latest spots, wikis and events. At the bottem of the app they can find a navigation bar, where they can decide to jump to the spotter or to events.
### Spotter page
Here the user is able to look up any car they'd like and, if it is not available, add it. Once a model is selected, information of the specific car model will be presented. At the bottem of this page, there is a 'Spots' button. When clicked upon, they can scroll through all the times the car has been spotted with information on time and location. From this list there are two possible choices:
* At the bottem of the 'Spots' page the user can select to add a spot, which will open a menu to upload an image of the spotted car and a button to save the location of the user.
* When a certain 'spot' is selected, a heatmap will open of all the spots. The map will also be focused on the selected 'spot', indicated with an extra marker.
### Events page
The right tab of the navigation bar takes the user to a new window where they can scroll through upcoming events. They have the option to look for specific events using a search bar or filter based on how soon the events will take place (within a week, month or year). At the bottem of this page, the user can again decide to upload their own event.
When a specific event is selected, information on the type of event, fee, time and location can be found above a description of the event itself.
### User page
On this page the user is able to login in. If the username is unknown, they'll be prompted to register. For security reasons we use a salted hash of the given password, which will only be checked inside the database.
Upon logging in the layout will change to a list of all the user's spots. Clicking on one of the spots will once again open a map, but this map only shows all the users spots instead of car specific spots.
When logged in and the user spots a car, a tag will be added in the lists of spot showing who made that specific spot.
### Demo
[🎥 Watch a demo of CarSpotter on YouTube](https://youtu.be/wEiyMij9MdQ)
## Important note
The API-key used for this project has been disabled for security purposes.
