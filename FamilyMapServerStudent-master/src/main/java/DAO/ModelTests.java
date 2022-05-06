package DAO;

import DAO.DataAccessException;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTests {

    AuthToken auth;
    Event event;
    Person person;
    User user;


    @BeforeEach
    public void setUp()
    {
        auth = new AuthToken("testAuth", "testUser");
        event = new Event("123", "test", "id", 1,1,"usa", "provo", "learn", 1000);
        person = new Person("id","user", "first", "last", "m", "dad", "mom", "wife");
        user = new User("username", "pass", "em", "first", "last", "m", "thing");
    }

    @Test
    public void tokenEqualsPass()
    {
        assertTrue(auth.equals(new AuthToken("testAuth", "testUser")));
    }
    @Test
    public void tokenEqualsFail()
    {
        assertFalse(auth.equals(new AuthToken("wrongAuth", "wrongUser")));
    }

    @Test
    public void eventEqualsPass()
    {
        assertTrue(event.equals(new Event("123", "test", "id", 1,1,"usa", "provo", "learn", 1000)));
    }
    @Test
    public void eventEqualsFail()
    {
        assertFalse(event.equals(new Event("1", "wrong", "no", 2,2,"no", "wrong", "nothing", 1003)));
    }

    @Test
    public void personEqualsPass()
    {
        assertTrue(person.equals(new Person("id","user", "first", "last", "m", "dad", "mom", "wife")));
    }
    @Test
    public void personEqualsFail()
    {
        assertFalse(person.equals(new Person("id2","user", "first", "last", "m", "dad", "mom", "wife")));
    }

    @Test
    public void userEqualsPass()
    {
        assertTrue(user.equals(new User("username", "pass", "em", "first", "last", "m", "thing")));
    }
    @Test
    public void userEqualsFail()
    {
        assertFalse(user.equals(new User("username", "pass", "em", "first", "lastWrong", "m", "thing")));
    }
}
