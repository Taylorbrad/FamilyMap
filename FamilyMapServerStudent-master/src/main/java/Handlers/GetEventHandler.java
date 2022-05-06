package Handlers;

import DAO.DataAccessException;
import Result.EventResult;
import Result.EventsResult;
import Services.GetEvent;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class GetEventHandler extends BaseHandle
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {


        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();

                URI test = (exchange.getRequestURI());

                //
                // We tokenize the url, and use its size to know what to do later.
                //
                String[] URL = test.toString().split("/");
                String eventID = null;

                if (URL.length > 2)
                {
                    eventID = URL[2];
                }
                //
                // we parse the Authorization header to get our authtoken, used
                // later to get the events only associated with that token
                //
                String inAuthToken = reqHeaders.get("Authorization").get(0);

                OutputStream resBody = exchange.getResponseBody();

                Gson gson = new Gson();

                GetEvent getEvent = new GetEvent();

                if (eventID == null)
                {
                    //
                    // if eventID is null, then we know we are getting all events. thats what this path is.
                    //

                    //System.out.println("getevenets" + inAuthToken);

                    EventsResult result = getEvent.getEvent(inAuthToken);

                    if (result != null)
                    {
                        //System.out.println("not null" + inAuthToken);
                        String response = gson.toJson(result);

                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                        writeString(response, resBody);

                        exchange.getResponseBody().close();
                        success = true;
                    }
                }
                else
                {
                    //
                    // otherwise, we get just the event associated with the passed in eventID
                    //

                    EventResult result = getEvent.getEvent(eventID, inAuthToken);


                    if (result != null)
                    {

                        String response = gson.toJson(result);

                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                        writeString(response, resBody);


                        exchange.getResponseBody().close();
                        success = true;
                    }
                }
            }

            //
            // if either fails, we sent the appropriate fail header and message
            //
            if (!success) {
                //System.out.println("Fail1");

                OutputStream resBody = exchange.getResponseBody();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

                writeString(getErrorJson("Lookup Events failed"), resBody);

                // We are not sending a response body, so close the response body
                // output stream, indicating that the response is complete.
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);

            exchange.getResponseBody().close();

            e.printStackTrace();
        }
        catch (DataAccessException d)
        {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);

            exchange.getResponseBody().close();

            d.printStackTrace();
        }

    }
}
