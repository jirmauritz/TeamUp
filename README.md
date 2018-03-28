# TeamUp
Project in Android development course PV239. 

Android app connecting people for sport activities. User creates sport events that belongs to one of these 10 categories:
1. Football
2. Hokey
3. Basketball
4. Floorball
5. Tenis
6. Table tenis
7. Voleyball
8. Badminton
9. Frisbee
10. other

The event consists of 
* name
* category
* location (address converted to GPS by SmartLocation)
* number of people to play
* number of missing people
* approximated cost

The main page shows a map with markers of all created events. After the user taps on a marker, new activity with event
details appears. User has an option to sign in for the event. In that case, number of missing people must decrease.
If the number of missing people equals to number of people to play, signing in must be disabled.

User signs in to the app by _Google_ account. All data are stored in _Firebase_ DB.
