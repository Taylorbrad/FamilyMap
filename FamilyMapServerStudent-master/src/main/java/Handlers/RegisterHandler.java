package Handlers;

import DAO.DataAccessException;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.RegisterResult;
import Services.Fill;
import Services.Register;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import com.google.gson.Gson;

public class RegisterHandler extends BaseHandle {

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
                // Create the request from the given JSON input data. Use this object to register the user.
                //
                RegisterRequest request = gson.fromJson(reqData, RegisterRequest.class);

                Register register = new Register();

                RegisterResult result = register.makeRequest(request);

                if (result != null)
                {

                    //
                    //once the user is registered, we want to call the fill method with 4 generations as per the specs.
                    //
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    Fill fill = new Fill();
                    fill.fill(request.getUsername(), "", 4);

                    String response = gson.toJson(result);

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

                writeString(getErrorJson(" Bad Request"), resBody);

                exchange.getResponseBody().close();


            }
        }
        catch (IOException e) {
            OutputStream resBody = exchange.getResponseBody();

            writeString(getErrorJson("Server Error: IOException"), resBody);

            exchange.getResponseBody().close();

            // Display/log the stack trace
            e.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
        }
        catch (DataAccessException d)
        {
            OutputStream resBody = exchange.getResponseBody();

            String response = getErrorJson("Server Error: Data Access Exception");
            writeString(response, resBody);
            exchange.getResponseBody().close();

            // Display/log the stack trace
            d.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
        }
    }
}
