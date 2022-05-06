package Services;
import DAO.*;
import Model.User;
import Request.LoadRequest;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.RegisterResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {

    Database db;
    Connection conn;
    Clear clear;
    Fill fill;
    GetEvent getEvent;
    GetPerson getPerson;
    Load load;
    Login login;
    Register register;

    RegisterRequest registerR = new RegisterRequest("username", "pass", "abc@cba.com", "steve", "harrington", "m");
    LoginRequest loginR = new LoginRequest("username", "pass");

    UserDAO uDAO;
    PersonDAO pDAO;
    EventDAO eDAO;
    AuthTokenDAO aDAO;
    GeneralDAO gDAO;

    Gson gson;

    String getRowsSQL;

    @BeforeEach
    public void setUp() throws IOException, DataAccessException {
        db = new Database("jdbc:sqlite:familymap.sqlite");
        conn = db.getConnection();

        clear = new Clear();
        fill = new Fill();
        getEvent = new GetEvent();
        getPerson = new GetPerson();
        load = new Load();
        login = new Login();
        register = new Register();

        uDAO = new UserDAO(conn);
        pDAO = new PersonDAO(conn);
        eDAO = new EventDAO(conn);
        aDAO = new AuthTokenDAO(conn);
        gDAO = new GeneralDAO(conn);

        gson = new Gson();
        //getRowsSQL = "SELECT COUNT(*) FROM ";

        //Start each test with a blank slate, so we clear the db
        clear.clear();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void registerPass() throws DataAccessException
    {

        register.makeRequest(registerR);


        assertNotNull(uDAO.find("username"), "Now it should not be null");
        //assertTrue(false);
    }
    @Test
    public void registerFail() throws DataAccessException
    {
        register.makeRequest(registerR);

        assertNotNull(uDAO.find("username"));

        //This returns null if it finds that someone with the same username is already registered
        assertNull(register.makeRequest(registerR));
    }

    @Test
    public void clearPass()
    {
        //Make sure it can clear even when DB is empty
        assertDoesNotThrow(() -> clear.clear());
    }
    @Test
    public void clearPass2() throws DataAccessException
    {

        //make sure it doesnt throw anything even if there is already data in the DB
        register.makeRequest(registerR);

        assertDoesNotThrow(() -> clear.clear());
    }

    @Test
    public void fillPass() throws DataAccessException
    {
        assertNotNull(register.makeRequest(registerR));

        assertDoesNotThrow(() -> fill.fill("username", "", 4));

        //make sure it added the right amount of people
        assertEquals(31,  pDAO.getAllPeople("username").getData().size());
    }
    @Test
    public void fillPass2() throws DataAccessException
    {
        assertNotNull(register.makeRequest(registerR));

        assertDoesNotThrow(() -> fill.fill("username", "", 2));

        //make sure it works with different values for 'generations'
        assertEquals(7,  pDAO.getAllPeople("username").getData().size());

    }

    @Test
    public void getEventsPass() throws DataAccessException
    {
        register.makeRequest(registerR);
        fill.fill("username", "", 2);


        assertEquals(19, getEvent.getEvent(aDAO.find("username").getAuthtoken()).data.size());
    }
    @Test
    public void getEventsPass2() throws DataAccessException
    {
        register.makeRequest(registerR);
        fill.fill("username", "", 4);

        //test with more events
        assertEquals(91, getEvent.getEvent(aDAO.find("username").getAuthtoken()).data.size());
    }

    @Test
    public void getEventPass() throws DataAccessException
    {
        register.makeRequest(registerR);
        fill.fill("username", "", 4);

        assertNotNull(getEvent.getEvent(eDAO.getAllEvents("username").data.get(0).getEventID(), aDAO.find("username").getAuthtoken()));
    }
    @Test
    public void getEventFail() throws DataAccessException
    {
        register.makeRequest(registerR);
        fill.fill("username", "", 2);

        assertNotNull(getEvent.getEvent(eDAO.getAllEvents("username").data.get(5).getEventID(), aDAO.find("username").getAuthtoken()));
    }

    @Test
    public void getPersonsPass() throws DataAccessException
    {
        assertNotNull(register.makeRequest(registerR));

        assertDoesNotThrow(() -> fill.fill("username", "", 4));

        //make sure it added the right amount of people
        assertEquals(31,  pDAO.getAllPeople("username").getData().size());
    }
    @Test
    public void getPersonsPass2() throws DataAccessException
    {
        assertNotNull(register.makeRequest(registerR));

        //make sure it added just one person, for the person who registered
        assertEquals(1,  pDAO.getAllPeople("username").getData().size());
    }

    @Test
    public void getPersonPass() throws DataAccessException
    {
        register.makeRequest(registerR);

        String personID = uDAO.find("username").getPersonID();

        String authToken = aDAO.find("username").getAuthtoken();

        assertEquals(registerR.getFirstName(), getPerson.getPerson(personID,authToken).firstName);
    }

    @Test
    public void getPersonFail() throws DataAccessException
    {
        register.makeRequest(registerR);

        //test with junk id and authtoken
        assertNull(getPerson.getPerson("wrongID", "wrongAuth"));
    }

    @Test
    public void loadPass() throws DataAccessException, IOException {

        String test = "";
        if (new File("passoffFiles/LoadData.json").isFile())
        {
            test = Files.readString(Path.of("passoffFiles/LoadData.json"));
        }

        load.load(gson.fromJson(test, LoadRequest.class));

        assertEquals(uDAO.find("sheila").getFirstName(), "Sheila");
    }
    @Test
    public void loadFail() throws DataAccessException
    {
        assertThrows(JsonSyntaxException.class, () -> load.load(gson.fromJson("Not real json", LoadRequest.class)));
    }

    @Test
    public void loginPass() throws DataAccessException
    {
        register.makeRequest(registerR);

        //if this passes, the result should have the password in it. compare that to the registerR username
        assertEquals(login.login(loginR).getUsername(), registerR.getUsername());
    }
    @Test
    public void loginFail() throws DataAccessException
    {
        register.makeRequest(registerR);

        //this should return null, because the login failed
        assertNull(login.login(new LoginRequest("wronguser", "wrongPass")));
    }


}
