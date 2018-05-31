# TeamUp
Project in Android development course PV239. 

Android app connecting people for sport activities. User creates sport events that consists of 
* name
* location (address converted to GPS by SmartLocation)
* maximum number of people to play
* current number of people
* names of the participants

The main page shows all upcoming events. User has an option to sign in for an event, which increases the current number of people to play. The sport event detail activity shows map with the location of the event and all necessary information. Other pages allow user to filter only events created by her, events that she will attend, or already attended events in the past.

User signs in to the app by _Google_ account. All data are stored in the _Firebase_ DB.
