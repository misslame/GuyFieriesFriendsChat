#include "../header/mongoose.h"  // Include Mongoose API definitions
#include <iostream>
#include <map>

std::map<struct mg_connection *, std::string> connectedClients; 

// Defined event handler
static void ev_handler(struct mg_connection *nc, int ev, void *p) {
	struct mbuf *io = &nc->recv_mbuf;
	if(ev == MG_EV_RECV){
		
		std::string m = std::string(io->buf, io->len);
		
		if( m.compare(0,4, "nick") == 0 ){ // message passed is a nickname addition. 
			// debug
			std::cout << "Nick Name Entered: " << m.substr(4) << std::endl;
			connectedClients[nc] = m.substr(4);
			
		}else{
			// TCP Echo
			for(auto i = connectedClients.begin(); i != connectedClients.end(); i++){
				std::string nick = connectedClients[nc];
				nick.append(": ");
				std::string message = nick.append(m);
				
				message.append("\n");
				if((i->first) != nc){
					mg_send((i->first), message.c_str(), message.length());
				}
			}
			
		}
		mbuf_remove(io, io->len);
		
		//mg_send(nc, io->buf, io->len);  
		//mbuf_remove(io, io->len); 
	}else if(ev == MG_EV_ACCEPT){
		connectedClients[nc] = "Guy Fieri Pal";
	}
}

int main(void) {
	struct mg_mgr mgr; // Manager
	mg_mgr_init(&mgr, NULL);  // Initialize event manager object

	mg_bind(&mgr, "42069", ev_handler);  // port is 42069 (IP IS WHERE SERVER IS RUN)

	// infinite event loop
	while(true){  
		mg_mgr_poll(&mgr, 1000); // polls for events.
	}

	mg_mgr_free(&mgr); // free memory
	
	return 0;
}