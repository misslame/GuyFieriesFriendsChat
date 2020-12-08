#include "../header/mongoose.h"  // Include Mongoose API definitions
#include <iostream>
#include <sstream>
#include <map>

// ***************************
//	CHANGE PORT HERE
// ***************************

const std::string PORT = "12345"; 

std::map<struct mg_connection *, std::string> connectedClients; 

// Represents a torn apart HTTP Request that can be easily understood. 
struct HTTPObject{ // default public attributes for HTTPObject 
	std::string requestType;
	std::string requestedAction;
	std::string host; // where it is hosted (must be server's host)
	std::string contentType; 
	int contentLength; // length of the message sent from the client
	std::string* otherParams; // non implemented params for the request
	std::string message; // What is sent from the client as extra fields
};

static HTTPObject* parse_http(std::string httpMessage){
	std::stringstream parseMessage(httpMessage);
	
	HTTPObject* request = new HTTPObject();
	int index = 0; // what index of the array to add to. 
	
	while(parseMessage.good()){
		std::string line; // what is read in by stringstream buffer
		getline(parseMessage, line, '\n');
		
		if(line.compare(0,4, "Host") == 0){
			// Init host attribute for request
			request->host = line.substr(6);
			
		}else if(line.compare(0,12, "Content-Type") == 0){
			// Init content type attribute for request
			request->contentType = line.substr(14);
			
		}else if(line.compare(0,14, "Content-Length") == 0){
			// Init content length attribute for request
			std::stringstream convert(line.substr(16));
			int converted;
			convert >> converted; // convert the string to an integer
			request->contentLength = converted;
			
		}else if(line.length() >= 1 && line[0] != '\n'){ // NOT IMPLEMENTED PARAMS
			// Append to otherParams for request. 
			
			if(request->otherParams == nullptr){ // if array has not been created. 
				request->otherParams = new std::string[1000];
			}
			request->otherParams[index] = line;
			index++;
		}
		
	}

	// Used to split string around spaces.
    std::istringstream parsey(request->otherParams[0]);
	
	// Get first non specific param of the request type (ex. POST/GET)
	parsey >> request->requestType;
	
	// Get the intended action to perform by the server (ex. /nickname)
	parsey >> request->requestedAction;
	request->otherParams[0] = "";

	// Get the last field that represents dynamic information (message sent/nickname entered)
	request->message = request->otherParams[index-1];
	request->otherParams[index-1] = "";
	
	//debug
	//std:: cout << std::endl << std::endl << request->requestType << " " << request->requestedAction << " " << request->message << std::endl;
	return request;
}

// Defined event handler
static void ev_handler(struct mg_connection *nc, int ev, void *p) {
	struct mbuf *io = &nc->recv_mbuf;
	if(ev == MG_EV_RECV){
		
		std::string m = std::string(io->buf, io->len);
		
		HTTPObject *request = parse_http(m); // Parses the HTTP Request to be readable
	
		if(request->requestType == "POST"){
			
			// debug OUTPUTS ENTIRE HTTP REQUEST FROM CLIENT
			std::cout << m; 
			
			if( request->requestedAction == "/nickname" ){ // message passed is a nickname addition. 
				
				connectedClients[nc] = request->message; // saves nickname to the clients connection signiture
			
			}else if( request->requestedAction == "/send_message"){ // message passed is a message to be sent to clients.
				
				// Loops through all connected clients to send message to all but the sender
				for(auto i = connectedClients.begin(); i != connectedClients.end(); i++){
					std::string nick = connectedClients[nc];
					nick.append(": ");
					std::string message = nick.append(request->message); // appends nickname to message
					
					message.append("\n");
					if((i->first) != nc){
						mg_send((i->first), message.c_str(), message.length());
					}
				}
				
				
			}
		}else{
			// ECHO IN CONSOLE OUTPUT
			std::cout << "Unrecognized Command: " << request->requestType << std::endl;
		}
		mbuf_remove(io, io->len); // clears buffer after event completed
		
	}else if(ev == MG_EV_ACCEPT){
		connectedClients[nc] = "Guy Fieri Pal"; // defaulted nickname for connected client
	}
}

int main(void) {
	struct mg_mgr mgr; // Manager
	mg_mgr_init(&mgr, NULL);  // Initialize event manager object

	mg_bind(&mgr, PORT.c_str(), ev_handler);  // port is 42069 (IP IS WHERE SERVER IS RUN)

	// infinite event loop
	while(true){  
		mg_mgr_poll(&mgr, 1000); // polls for events.
	}

	mg_mgr_free(&mgr); // free memory
	
	return 0;
}