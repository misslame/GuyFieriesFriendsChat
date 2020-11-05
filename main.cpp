#include "mongoose.h";
#include <string>
#include <iostream>

static struct mg_serve_http_opts http_connection;

int main(void) {

	int port_number;

	port_number = 1000;

	Server(port_number);

	return 0;


}

int Server(int entry_port) {

	struct mg_mgr manager;

	struct mg_connection* conn;

	std::string portActual = std::to_string(entry_port);
	static char const* fPort = portActual.c_str();

	mg_mgr_init(&manager, NULL);
	std::cout << "Beginning web server on port 1000 " << fPort << std::endl;

	conn = mg_bind(&manager, fPort, event_handler);

	if (conn == NULL) {
		std::cout << "Connection to listener has failed" << std::endl;
		return 1;

	}

	mg_set_protocol_http_websocket(conn);

	http_connection.document_root = ".";

	http_connection.enable_directory_listing = "yes";

	while (true) {

		mg_mgr_poll(&manager, 1000);

	}

	mg_mgr_free(&manager);

	return 0;


}

static void event_handler(struct mg_connection* conn, int event, void* g) {
	// If event is a http request
	if (event == MG_EV_HTTP_REQUEST) {
		// server static html files
		mg_serve_http(conn, (struct http_message*) g, http_connection);
	}
}


