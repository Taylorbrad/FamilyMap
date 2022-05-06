package Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import Result.ErrorResultUnused;

import java.io.*;

public abstract class BaseHandle implements HttpHandler {

    String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    String getErrorJson(String errorMessage)
    {
        Gson gson = new Gson();
        return gson.toJson(new ErrorResultUnused("Error: " + errorMessage));
    }

}
