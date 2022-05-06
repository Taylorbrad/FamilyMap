package Handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler extends BaseHandle {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        String filePath = null;
        String reqData = null;

        //System.out.println("File Handler");

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                //
                // we get the URL and return the appropriate web page file
                //
                String urlPath = exchange.getRequestURI().toString();

                if (urlPath == null || urlPath.equals("/"))
                {
                    urlPath = "/index.html";
                    filePath = "web" + urlPath;


                    success = true;
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else if (new File("web" + urlPath).isFile())
                {
                    filePath = "web" + urlPath;
                    success = true;
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else
                {
                    urlPath = "/HTML/404.html";
                    filePath = "web" + urlPath;
                    success = false;
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                }
                OutputStream resBody = exchange.getResponseBody();

                Files.copy(new File(filePath).toPath(), resBody);
            }
        }


        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);

            exchange.getResponseBody().close();

            // Display/log the stack trace
            e.printStackTrace();
        }

        //
        // we already handled the HTTP errors, so the failure cases just close the response body.
        //
        if (!success) {
            exchange.getResponseBody().close();

        }
        else
        {
            exchange.getResponseBody().close();
        }
    }
}
