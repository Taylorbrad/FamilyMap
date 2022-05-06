package Handlers;

import DAO.DataAccessException;
import Request.LoginRequest;
import Result.ClearResult;
import Result.LoginResult;
import Services.Clear;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class ClearHandler extends BaseHandle {


    @Override
    public void handle(HttpExchange exchange) throws IOException
    {

        boolean success = false;

        OutputStream resBody = exchange.getResponseBody();
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                //
                // if the request is in fact a POST, we run the request
                //
                Gson gson = new Gson();

                Clear clear = new Clear();

                ClearResult result = clear.clear();

                if (result != null)
                {
                    //
                    // if it succeeds, we return an HTTP_OK message, along with a success response body
                    //
                    String response = gson.toJson(result);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    writeString(response, resBody);


                    exchange.getResponseBody().close();
                    success = true;
                }
            }

            //
            // if it fails, we send a failure header, and a failure response body.
            //
            if (!success) {

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

                writeString(getErrorJson("Clear failed."), resBody);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

            writeString(getErrorJson("Clear failed due to Data Access error."), resBody);

            exchange.getResponseBody().close();

            e.printStackTrace();
        }
        catch (DataAccessException d)
        {

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

            writeString(getErrorJson("Clear failed due to a server error"), resBody);
            exchange.getResponseBody().close();

            d.printStackTrace();
        }
    }
}
