# GuyFieriesFriendsChat
Web Server Chat Application that pays tribute to and is themed after the Diners, Drive-Ins and Dives host, Guy Fieri. 

## About:
 Private chat room application for chatting with others over a (non-secure) network connection to a server. 
  ##### Client Tools & Languages:
   Ui is done with JavaFX and FXML/Utilizes threads for receiving messages/Controller handles UI events
  #### Server Tools & Languages:
  C++ utilizing the open sourced networking library called Mongoose by cesanta. (HTTP Parsing is manually done instead of using library) The library and more information can be found [here](https://github.com/cesanta/mongoose "Cesanta Github")
  
    
## How to :
  ### (Host your own private guy fieri chat room yourself)
  
  #### Run the Client:
  1. Following the file path: .\chatapp\ChatApp\src\assets\ place a .txt file called "server_info.txt" in this directory
  2. Inside "server_info.txt" put your desired IP/Port that represents the server for the client chat application to connect to
  3. Ensure you have the JavaFX SDK : [Download instructions here](https://www.oracle.com/java/technologies/install-javafx-sdk.html)
  4. Run the client application. 
  
  #### Run the Server:
  1. Following the file path: .\server\source\ open up main.cpp
  2. Look for a large comment at the top of the file saying "CHANGE PORT HERE"
  3. Change constant for PORT to the designated port you prefer. By default it is 12345. Save file. 
  4. Open terminal on parent directory and run make, and then the executable a.out 

**We do not claim ownership of anything Guy Fieri themed and this project is in no way sponsored or approved by Guy Fieri or related teams/officials.** 
