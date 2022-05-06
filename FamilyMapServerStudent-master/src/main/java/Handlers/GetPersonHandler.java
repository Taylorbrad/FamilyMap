package Handlers;

import DAO.DataAccessException;
import Result.PersonResult;
import Result.PersonsResult;
import Services.GetPerson;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class GetPersonHandler extends BaseHandle
{

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {


        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                //
                // if it is a GET request, do this
                //
                Headers reqHeaders = exchange.getRequestHeaders();

                URI test = (exchange.getRequestURI());

                String[] URL = test.toString().split("/");

                //
                // if the personID is null, we know we want to get all persons.
                //

                String personID = null;

                if (URL.length > 2)
                {
                    personID = URL[2];
                }
                OutputStream resBody = exchange.getResponseBody();

                Gson gson = new Gson();

                GetPerson getPerson = new GetPerson();

                String inAuthToken = reqHeaders.get("Authorization").get(0);

                if (personID == null)
                {
                    //
                    // This path runs the get all people method.
                    //

                    PersonsResult result = getPerson.getAllPersons(inAuthToken);

                    if (result != null)
                    {
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
                    // This is where we get just a single person, based on the passed in personID
                    //
                    PersonResult result = getPerson.getPerson(personID, inAuthToken);

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
            // if either fails, we send the appropriate failure headers and message
            //
            if (!success) {

                OutputStream resBody = exchange.getResponseBody();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

                writeString(getErrorJson("Lookup Events failed"), resBody);

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
