#include "../header/mongoose.h"  // Include Mongoose API definitions
#include <iostream>
#include <vector>

std::vector<struct mg_connection *> connectedClients; 

// Define an event handler function
static void ev_handler(struct mg_connection *nc, int ev, void *p) {
	struct mbuf *io = &nc->recv_mbuf;
	if(ev == MG_EV_RECV){
		// debug
		std::cout << std::string(io->buf, io->len) << std::endl;
	  
		// TCP Echo
		for(auto i = connectedClients.begin(); i != connectedClients.end(); i++){
			if(*i != nc){
				mg_send(*i, io->buf, io->len);
			}
		}
		mbuf_remove(io, io->len);
		
		//mg_send(nc, io->buf, io->len);  
		//mbuf_remove(io, io->len); 
	}else if(ev == MG_EV_ACCEPT){
		connectedClients.push_back(nc);	
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