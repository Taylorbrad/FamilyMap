package Handlers;

import java.io.*;
import java.net.*;

import DAO.DataAccessException;
import Request.LoginRequest;
import Result.LoginResult;
import Services.Login;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;

/*
	The ClaimRouteHandler is the HTTP handler that processes
	incoming HTTP requests that contain the "/routes/claim" URL path.

	Notice that ClaimRouteHandler implements the HttpHandler interface,
	which is define by Java.  This interface contains only one method
	named "handle".  When the HttpServer object (declared in the Server class)
	receives a request containing the "/routes/claim" URL path, it calls
	ClaimRouteHandler.handle() which actually processes the request.
*/
public class LoginHandler extends BaseHandle {

    @Override
    public void handle(HttpExchange exchange) throws IOException {


        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();

                // Read JSON string from the input stream
                String reqData = readString(reqBody);
                OutputStream resBody = exchange.getResponseBody();

                Gson gson = new Gson();

                //
                // create a new LoginRequest with the data from the reqData input JSON.
                // use that object to actually log in, and get the LoginResult
                //
                LoginRequest request = gson.fromJson(reqData, LoginRequest.class);

                Login login = new Login();

                LoginResult result = login.login(request);

                if (result != null)
                {
                    String response = gson.toJson(result);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    writeString(response, resBody);

                    exchange.getResponseBody().close();
                    success = true;
                }


            }

            //
            // if this fails, send the appropriate failure messages and headers
            //
            if (!success) {
                OutputStream resBody = exchange.getResponseBody();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

                System.out.println("Error");
                writeString(getErrorJson("Error: Login failed. Incorrect Username or Password"), resBody);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            System.out.println("Error");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
        catch (DataAccessException d)
        {
            System.out.println("Error");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            d.printStackTrace();
        }
    }
}
