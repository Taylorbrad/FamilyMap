package Client;

import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.gson.Gson;

import Model.Event;
import Model.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.EventResult;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonResult;
import Result.PersonsResult;
import Result.RegisterResult;

public class ServerProxy {

    String serverHost;
    String serverPort;

    public ServerProxy(String host, String port)
    {
        serverHost = host;
        serverPort = port;
    }

    public LoginResult login(LoginRequest request)
    {

        try {
            // Create a URL indicating where the server is running
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");

            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);	// There is a request body

            // Connect to the server and send the HTTP request
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            String reqData =
                    "{" +
                            "\"username\":\"" + request.getUsername() + "\",\n" +
                            "\"password\":\"" + request.getPassword() +
                            "\"\n}";

            System.out.println(reqData);

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(reqData, reqBody);

            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                DataCache cache = DataCache.getInstance();

                InputStream resBody = http.getInputStream();
                Gson gson = new Gson();

                LoginResult result = gson.fromJson(readString(resBody), LoginResult.class);
                cache.setAuthToken(result.getAuthtoken());
                cache.setPersonID(result.getPersonID());

                this.getPersons(serverHost,serverPort);
                this.getEvents(serverHost,serverPort);


                http.disconnect();
                return result;
            }
            else {
                DataCache cache = DataCache.getInstance();

                cache.setAuthToken("empty");


                http.disconnect();
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();

        }
        return null;
    }

    public RegisterResult register(RegisterRequest request)
    {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);


            http.connect();

            String reqData =
                    "{" +
                            "\"username\":\"" + request.getUsername() + "\",\n" +
                            "\"password\":\"" + request.getPassword() + "\",\n" +
                            "\"email\":\"" + request.getEmail() + "\",\n" +
                            "\"firstName\":\"" + request.getFirstName() + "\",\n" +
                            "\"lastName\":\"" + request.getLastName() + "\",\n" +
                            "\"gender\":\"" + request.getGender() +
                            "\"\n}";

            OutputStream reqBody = http.getOutputStream();

            writeString(reqData, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                DataCache cache = DataCache.getInstance();

                InputStream resBody = http.getInputStream();
                Gson gson = new Gson();

                RegisterResult result = gson.fromJson(readString(resBody), RegisterResult.class);
                cache.setPersonID(result.personID);
                cache.setAuthToken(result.authtoken);
                cache.setFirstName(request.getFirstName());
                cache.setLastName(request.getLastName());



                this.getPersons(serverHost,serverPort);
                this.getEvents(serverHost,serverPort);




                return result;
            }
            else {
                DataCache cache = DataCache.getInstance();

                cache.setPersonID("empty");

                http.disconnect();

                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return null;
    }

    public PersonResult getPerson(String personID, String authToken)
    {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/" + personID);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", authToken);

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                DataCache cache = DataCache.getInstance();

                InputStream resBody = http.getInputStream();
                Gson gson = new Gson();

                PersonResult result = gson.fromJson(readString(resBody), PersonResult.class);
                cache.setPersonID(result.personID);
                cache.setFirstName(result.firstName);
                cache.setLastName(result.lastName);


                http.disconnect();

                return result;
            }
            else {
                DataCache cache = DataCache.getInstance();

                cache.setPersonID("empty");


                http.disconnect();

                System.out.println("ERROR: " + http.getResponseMessage());


                InputStream respBody = http.getErrorStream();


                String respData = readString(respBody);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return null;
    }

    public PersonsResult getPersons(String serverHost, String serverPort)
    {

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");


            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", DataCache.getInstance().getAuthToken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                DataCache cache = DataCache.getInstance();

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                Gson gson = new Gson();

                PersonsResult result = gson.fromJson(respData, PersonsResult.class);

                ArrayList<Person> persons = result.getData();

                HashMap<String, Person> personsByID = new HashMap<>();

                for (int i = 0; i < persons.size(); ++i)
                {
                    Person person = persons.get(i);
                    personsByID.put(person.getPersonID(), person);
                }
                cache.setFatherID(personsByID.get(cache.getPersonID()).getFatherID());
                cache.setMotherID(personsByID.get(cache.getPersonID()).getMotherID());
                cache.setPersonsByID(personsByID);
                cache.setPersons(persons);


            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return null;
    }

    public EventsResult getEvents(String serverHost, String serverPort)
    {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", DataCache.getInstance().getAuthToken());


            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                DataCache cache = DataCache.getInstance();

                InputStream resBody = http.getInputStream();

                Gson gson = new Gson();

                EventsResult result = gson.fromJson(readString(resBody), EventsResult.class);

                ArrayList<Event> eventList = result.getData();

                cache.setEvents(eventList);

                cache.motherSideIDs.add(cache.getMotherID());
                cache.fatherSideIDs.add(cache.getFatherID());
                cache.setMotherSideIDs(cache.getMotherID());
                cache.setFatherSideIDs(cache.getFatherID());


                HashMap<String, Person> personsByIDmap = cache.getPersonsByID();

                HashMap<String, ArrayList<Event>> lifeEventsByPersonID = new HashMap<>();
                HashMap<String, Event> spouseBirthMap = new HashMap<>();


                for (Object key : personsByIDmap.keySet())
                {
                    Person person = personsByIDmap.get(key);

                    String spouseID = person.getSpouseID();
                    String personID = person.getPersonID();

                    ArrayList<Event> personsLifeEvents = new ArrayList<>();

                    Event spouseBirth = null;

                    for (Event event : eventList)
                    {
                        if (event.getPersonID().equals(spouseID))
                        {
                            if(spouseBirth == null || event.getYear() < spouseBirth.getYear())
                            {
                                spouseBirth = event;
                            }

                        }
                        if (event.getPersonID().equals(personID))
                        {
                            personsLifeEvents.add(event);
                        }
                    }

                    spouseBirthMap.put(personID, spouseBirth);
                    Collections.sort(personsLifeEvents, (o1, o2) -> o1.getYear() - o2.getYear());

                    lifeEventsByPersonID.put(personID, personsLifeEvents);
                }
                for (Event event : eventList)
                {
                    for (String mID : cache.getMotherSideIDs())
                    {
                        if (event.getPersonID().equals(mID))
                        {
                            cache.getMotherSideEvents().add(event);
                        }
                    }
                    for (String fID : cache.getFatherSideIDs())
                    {
                        if (event.getPersonID().equals(fID))
                        {
                            cache.getFatherSideEvents().add(event);
                        }
                    }
                }

                cache.setPersonLifeEventsMap(lifeEventsByPersonID);
                cache.setSpouseBirthMap(spouseBirthMap);

                http.disconnect();

                return result;
            }
            else {


                http.disconnect();

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println("Error" + respData);
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return null;
    }
    public void clearDatabase() throws IOException {
        URL url = new URL("http://" + serverHost + ":" + serverPort + "/clear");

        HttpURLConnection http = (HttpURLConnection)url.openConnection();

        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.connect();

        if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream respBody = http.getInputStream();
        }
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
