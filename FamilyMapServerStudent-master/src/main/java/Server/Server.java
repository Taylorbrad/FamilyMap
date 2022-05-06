package Server;

import java.awt.desktop.SystemEventListener;
import java.io.*;
import java.net.*;

import Services.Clear;
import com.sun.net.httpserver.*;

import Handlers.*;

/*
	This example demonstrates the basic structure of the Family Map Server
	(although it is for a fictitious "Ticket to Ride" game, not Family Map).
	The example is greatly simplfied to help you more easily understand the
	basic elements of the server.

	The Server class is the "main" class for the server (i.e., it contains the
		"main" method for the server program).
	When the server runs, all command-line arguments are passed in to Server.main.
	For this server, the only command-line argument is the port number on which
		the server should accept incoming client connections.
*/
public class Server {

    private static final int MAX_WAITING_CONNECTIONS = 12;

    private HttpServer server;

    private void run(String portNumber) {

        System.out.println("Initializing HTTP Server");

        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //System.out.println( server.getAddress());

        server.setExecutor(null);

        System.out.println("Creating contexts");

        //
        // this section creates each of the options, based on the passed in URL. Creates instances of services for what we want to do.
        //
        server.createContext("/user/register", new RegisterHandler());

        server.createContext("/user/login", new LoginHandler());

        server.createContext("/clear", new ClearHandler());

        server.createContext("/fill", new FillHandler());

        server.createContext("/person", new GetPersonHandler());

        server.createContext("/event", new GetEventHandler());

        server.createContext("/load", new LoadHandler());

        server.createContext("/", new FileHandler());

        // Create and install the HTTP handler for the "/routes/claim" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/routes/claim" URL path, it will forward the request to ClaimRouteHandler
        // for processing.
        //server.createContext("/routes/claim", new ClaimRouteHandler());




        System.out.println("Starting server");
        server.start();
        System.out.println("Server started on port: " + portNumber);
    }

    public static void main(String[] args) {
        String portNumber = args[0];
        new Server().run(portNumber);
    }
}

