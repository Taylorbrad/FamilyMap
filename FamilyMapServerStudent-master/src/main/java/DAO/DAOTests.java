package DAO;

import Model.AuthToken;
import Model.Event;
import Model.Person;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class DAOTests {
    private Database db;
    private Event bestEvent;
    private Person bestPerson;
    private User bestUser;
    private AuthToken bestToken;
    private EventDAO eDao;
    private PersonDAO pDao;
    private UserDAO uDao;
    private AuthTokenDAO aDao;
    private GeneralDAO gDao;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database("jdbc:sqlite:familymap.sqlite");
        //and a new event with random data
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);

        bestUser = new User("myBoi", "pass123", "abc@abc.com", "Mark", "Bradshaw", "m", "test");

        bestPerson = new Person("myPerson", "myBoi", "Mark", "Bradshaw", "m", "", "", "");

        bestToken = new AuthToken("1234", "boi");

        //Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Let's clear the database as well so any lingering data doesn't affect our tests
        //clearTables();
        //Then we pass that connection to the EventDAO so it can access the database
        eDao = new EventDAO(conn);
        uDao = new UserDAO(conn);
        pDao = new PersonDAO(conn);
        aDao = new AuthTokenDAO(conn);
        gDao = new GeneralDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        //Here we close the connection to the database file so it can be opened elsewhere.
        //We will leave commit to false because we have no need to save the changes to the database
        //between test cases
        db.closeConnection(false);
    }

    @Test
    public void insertTokenPass() throws DataAccessException
    {
        aDao.insert(bestToken);

        AuthToken compareTest = aDao.find(bestToken.getUsername());

        assertNotNull(compareTest);

        assertEquals(bestToken, compareTest);
    }

    @Test
    public void insertTokenFail() throws DataAccessException
    {
        aDao.insert(bestToken);

        assertThrows(DataAccessException.class, ()-> aDao.insert(bestToken));
    }

    @Test
    public void insertEventPass() throws DataAccessException {
        //While insert returns a bool we can't use that to verify that our function actually worked
        //only that it ran without causing an error
        eDao.insert(bestEvent);
        //So lets use a find method to get the event that we just put in back out
        Event compareTest = eDao.find(bestEvent.getEventID());
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
        assertEquals(bestEvent, compareTest);
    }


    @Test
    public void insertEventFail() throws DataAccessException {
        //lets do this test again but this time lets try to make it fail
        //if we call the method the first time it will insert it successfully
        eDao.insert(bestEvent);
        //but our sql table is set up so that "eventID" must be unique. So trying to insert it
        //again will cause the method to throw an exception
        //Note: This call uses a lambda function. What a lambda function is is beyond the scope
        //of this class. All you need to know is that this line of code runs the code that
        //comes after the "()->" and expects it to throw an instance of the class in the first parameter.
        assertThrows(DataAccessException.class, ()-> eDao.insert(bestEvent));
    }

    @Test
    public void insertUserPass() throws DataAccessException {
        uDao.insert(bestUser);

        User compareTest = uDao.find(bestUser.getUsername());

        assertNotNull(compareTest);

        assertEquals(bestUser, compareTest);
    }

    @Test
    public void insertUserFail() throws DataAccessException
    {
        uDao.insert(bestUser);

        assertThrows(DataAccessException.class, ()-> uDao.insert(bestUser));
    }

    @Test
    public void findUserPass() throws DataAccessException
    {
        //Make sure it doesnt find the ID before being inserted
        assertNull(uDao.find("myBoi"));

        //Insert the person with "myBoi" ID.
        uDao.insert(bestUser);

        //Make sure it DOES find the ID now that its been inserted
        User compareTest = uDao.find("myBoi");

        assertNotNull(compareTest);

        assertEquals(bestUser, compareTest);
    }

    @Test
    public void findUserFail() throws DataAccessException
    {
        //Try to find an ID that has not been inserted into the database
        assertNull(uDao.find("FakeID"));

        //insert a person, then try and find another fake ID to assure it isnt finding the wrong object.\
        uDao.insert(bestUser);

        assertNull(uDao.find("FakeID2"));
    }

    @Test
    public void clearUsersPass() throws DataAccessException
    {
        uDao.insert(bestUser);

        User compareTest = uDao.find(bestUser.getUsername());

        assertNotNull(compareTest);

        assertEquals(bestUser, compareTest);

        uDao.clear();

        assertNull(uDao.find(bestUser.getUsername()));
    }
    @Test
    public void clearUsersPass2() throws DataAccessException
    {
        assertDoesNotThrow(() -> uDao.clear());
    }

    @Test
    public void addParentsPass() throws DataAccessException
    {
        pDao.insert(bestPerson);

        pDao.addParentsToPerson("myPerson", "father", "mother");

        assertTrue(pDao.find("myPerson").getMotherID().equals("mother"));
    }

    @Test
    public void addParentsFail() throws DataAccessException
    {
        pDao.insert(bestPerson);

        pDao.addParentsToPerson("myPerson", "father", "");

        assertTrue(pDao.find("myPerson").getMotherID().equals(""));
    }

    @Test
    public void insertPersonPass() throws DataAccessException
    {
        pDao.insert(bestPerson);

        Person compareTest = pDao.find(bestPerson.getPersonID());

        assertNotNull(compareTest);

        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void insertPersonFail() throws DataAccessException
    {
        pDao.insert(bestPerson);

        assertThrows(DataAccessException.class, ()-> pDao.insert(bestPerson));
    }

    @Test
    public void findPersonPass() throws DataAccessException
    {
        //Make sure it doesnt find the ID before being inserted
        assertNull(pDao.find("myPerson"));

        //Insert the person with "myPerson" ID.
        pDao.insert(bestPerson);

        //Make sure it DOES find the ID now that its been inserted
        Person compareTest = pDao.find("myPerson");

        assertNotNull(compareTest);

        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void findPersonFail() throws DataAccessException
    {
        //Try to find an ID that has not been inserted into the database
        assertNull(pDao.find("FakeID"));

        //insert a person, then try and find another fake ID to assure it isnt finding the wrong object.\
        pDao.insert(bestPerson);

        assertNull(pDao.find("FakeID2"));
    }

    @Test
    public void clearPeoplePass() throws DataAccessException
    {
        pDao.insert(bestPerson);

        Person compareTest = pDao.find(bestPerson.getPersonID());

        assertNotNull(compareTest);

        assertEquals(bestPerson, compareTest);

        pDao.clear();

        assertNull(pDao.find(bestPerson.getPersonID()));

    }

    @Test
    public void clearPeoplePass2() throws DataAccessException
    {
        Person test = new Person("test", "test", "test", "test", "f", "test", "test", "test");
        pDao.insert(test);

        Person compareTest = pDao.find("test");

        assertNotNull(compareTest);

        assertEquals(test, compareTest);

        pDao.clear();

        assertNull(pDao.find(test.getPersonID()));
    }

    @Test
    public void clearTokensPass() throws DataAccessException
    {
        aDao.insert(bestToken);

        assertNotNull(aDao.findByToken(bestToken.getAuthtoken()));

        aDao.clear();

        assertNull(aDao.findByToken(bestToken.getAuthtoken()));
    }
    @Test
    public void clearTokensPass2() throws DataAccessException
    {
        assertDoesNotThrow(() -> aDao.clear());
    }
    @Test
    public void findTokenFail() throws DataAccessException
    {
        assertNull(aDao.findByToken("NoTokens"));
    }
    @Test
    public void findTokenPass() throws DataAccessException
    {
        aDao.insert(bestToken);

        assertNotNull(aDao.findByToken(bestToken.getAuthtoken()));
    }

    @Test
    public void findEventPass() throws DataAccessException
    {
        eDao.insert(bestEvent);

        assertEquals(bestEvent, eDao.find(bestEvent.getEventID()));
        //assertTrue(false);
    }
    @Test
    public void findEventFail() throws DataAccessException
    {
        assertNull(eDao.find("notanevent"));
        //assertTrue(false);
    }
    @Test
    public void clearEventPass() throws DataAccessException
    {
        eDao.insert(bestEvent);

        assertNotNull(eDao.find(bestEvent.getEventID()));

        eDao.clear();

        assertNull(eDao.find(bestEvent.getEventID()));
    }
    @Test
    public void clearEventPass2() throws DataAccessException
    {
        assertDoesNotThrow(() -> eDao.clear());
    }
    @Test
    public void getAllEventsPass() throws DataAccessException
    {
        eDao.insert(bestEvent);

        assertEquals(1, eDao.getAllEvents("Gale").getData().size());
    }
    @Test
    public void getAllEventsFail() throws DataAccessException
    {
        assertNull(eDao.getAllEvents("myBoi"));
    }


    @Test
    public void customUpdatePass() throws DataAccessException
    {
        pDao.insert(bestPerson);
        String sql = "UPDATE Persons SET FatherID = 'Dad' WHERE PersonID = 'myPerson';";
        gDao.customUpdateSQL(sql);

        assertEquals(pDao.find("myPerson").getFatherID(), "Dad");
    }
    @Test
    public void customUpdateFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> gDao.customUpdateSQL("garba/ge; sql "));
    }


    @Test
    public void getAllPeoplePass() throws DataAccessException
    {
        pDao.insert(bestPerson);

        assertEquals(1, pDao.getAllPeople("myBoi").data.size());
    }
    @Test
    public void getAllPeopleFail() throws DataAccessException
    {
        assertNull(pDao.getAllPeople("myBoi"));
    }
}
