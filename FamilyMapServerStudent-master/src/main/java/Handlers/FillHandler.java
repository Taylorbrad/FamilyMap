package Handlers;

import DAO.DataAccessException;
import Result.ClearResult;
import Result.FillResult;
import Services.Fill;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class FillHandler extends BaseHandle {


    @Override
    public void handle(HttpExchange exchange) throws IOException //TODO fix the locations portion of FILL
    {

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                URI test = (exchange.getRequestURI());

                //
                // We get the URL, split it based on how many slashed it has, and use that as a
                // way to decipher which method calls to use.
                //

                String[] urlArray = test.toString().split("/");

                String username = urlArray[2];
                String generations = null;
                if (urlArray.length > 3)
                {
                    generations = urlArray[3];
                }
                else
                {
                    generations = "4";
                }

                OutputStream resBody = exchange.getResponseBody();

                Gson gson = new Gson();

                Fill fill = new Fill();

                int intGens = Integer.parseInt(generations);

                fill.fill(username, "", intGens);

                int amtPeople = ((int)Math.pow(2,intGens+1)-1);

                //
                // We called the fill method, and now we generate the result message based on the mathematically determined events and people added.
                //

                FillResult result = new FillResult("Successfully added " +  String.valueOf(amtPeople) + " persons and " + String.valueOf(amtPeople*3-2) + " events to the database.", true);

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
            // if it fails, we send the appropriate fail header and message
            //
            if (!success) {
                OutputStream resBody = exchange.getResponseBody();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

                writeString(getErrorJson("Fill failed."), resBody);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            OutputStream resBody = exchange.getResponseBody();

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);

            writeString(getErrorJson("Fill failed due to a database error"), resBody);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
        catch (DataAccessException d)
        {
            OutputStream resBody = exchange.getResponseBody();

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);

            writeString(getErrorJson("Fill failed due to a Server error"), resBody);
            exchange.getResponseBody().close();

            d.printStackTrace();
        }
    }
}
