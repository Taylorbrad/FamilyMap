package Handlers;

import DAO.DataAccessException;
import Request.LoadRequest;
import Result.ClearResult;
import Result.LoadResult;
import Services.Load;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoadHandler extends BaseHandle {

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();

                String reqData = readString(reqBody);
                OutputStream resBody = exchange.getResponseBody();

                Gson gson = new Gson();

                //
                // This gets the json from reqData, and creates a LoadRequest object. this object
                // is further used to actually run the load method, and get a LoadResult
                //

                LoadRequest request = gson.fromJson(reqData, LoadRequest.class);

                Load load = new Load();

                LoadResult result = load.load(request);

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
            // if this fails, return the appropriate failure messages
            //
            if (!success) {
                OutputStream resBody = exchange.getResponseBody();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

                writeString(getErrorJson("Load failed."), resBody);
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
