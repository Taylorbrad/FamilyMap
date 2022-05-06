package com.example.familymapclientmarktb;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

import android.provider.ContactsContract;

import org.junit.Before;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Client.DataCache;
import Client.ServerProxy;
import Model.Event;
import Model.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.RegisterResult;

public class TestingServer {

    ServerProxy proxy;
    DataCache cache = DataCache.getInstance();
    String personID;

    @Before
    public void setUp() throws IOException {
        proxy = new ServerProxy("localhost", "8080");
//        URL url = new URL("http://localhost" + ":8080" + "/user/login");
        proxy.clearDatabase();
        RegisterRequest register = new RegisterRequest("sheila", "parker", "email", "first", "last", "m");
        RegisterResult result = proxy.register(register);

        personID = result.personID;
    }

    @After
    public void breakDown() throws IOException {
//        proxy.clearDatabase();
    }

    @Test
    public void registerSuccess() throws IOException {
        proxy.clearDatabase();
        RegisterRequest register = new RegisterRequest("sheila", "parker", "email", "first", "last", "m");
        RegisterResult result = proxy.register(register);

        assertNotNull(result);
    }
    @Test
    public void registerFail()
    {
        RegisterRequest register = new RegisterRequest("username", "password", "email", "first", "last", "/m");
        RegisterResult result = proxy.register(register);

        assertNull(result);
    }
    @Test
    public void loginSuccess() throws IOException {
        LoginRequest login = new LoginRequest("sheila", "parker");
        LoginResult result = proxy.login(login);

        assertNotNull(result);

    }
    @Test
    public void loginFail()
    {
        LoginRequest login = new LoginRequest("notauser", "notapassword");

        assertNull(proxy.login(login));
    }



    @Test
    public void getPeople()
    {
        ArrayList<Person> saved = DataCache.getInstance().getPersons();

        assertNotNull(saved.get(0));
    }
    @Test
    public void getPeople2()
    {
        ArrayList<Person> saved = DataCache.getInstance().getPersons();

        Person testPerson = saved.get(0);

//        System.out.println( testPerson.getFirstName());

        assertEquals(testPerson.getFirstName(), "first");
    }

    @Test
    public void getEvents()
    {
        ArrayList<Event> saved = DataCache.getInstance().getEvents();

        assertNotNull(saved.get(0));
    }
    @Test
    public void getEvents2()
    {
        ArrayList<Event> saved = DataCache.getInstance().getEvents();

        Event test = saved.get(0);

        assertNotNull(test.getEventType());
    }

    @Test
    public void getFamilyMembers()
    {
        ArrayList<Person> fam = cache.getPersonsFamilyMembersByID(personID);

        assertEquals(2, fam.size());
    }
    @Test
    public void getFamilyMembersNull()
    {
        Person person = cache.getPersonByID(personID);
        Person parent = cache.getPersonByID(person.getFatherID());

        ArrayList<Person> fam = cache.getPersonsFamilyMembersByID(parent.getPersonID());

        assertEquals(6, fam.size());
    }

    @Test
    public void getPersonLifeEvents()
    {
        Person person = cache.getPersonByID(personID);
        Person parent = cache.getPersonByID(person.getFatherID());
        ArrayList<Event> personEvents = cache.getPersonLifeEventsByID().get(parent.getPersonID());

        assertEquals(3, personEvents.size());
    }
    @Test
    public void getPersonLifeEventsOrder()
    {
        Person person = cache.getPersonByID(personID);
        Person parent = cache.getPersonByID(person.getFatherID());
        ArrayList<Event> personEvents = cache.getPersonLifeEventsByID().get(parent.getPersonID());

        assertEquals("Birth", personEvents.get(0).getEventType());
        assertEquals("Marriage", personEvents.get(1).getEventType());
        assertEquals("Death", personEvents.get(2).getEventType());
    }

    @Test
    public void searchPersons()
    {
        ArrayList<Person> personsToSearch = cache.getPersons();

        ArrayList<Person> foundPersons = cache.searchPersons(personsToSearch, "first");

        assertEquals(1, foundPersons.size());
    }
    @Test
    public void searchPersons2()
    {
        ArrayList<Person> personsToSearch = cache.getPersons();

        ArrayList<Person> foundPersons = cache.searchPersons(personsToSearch, "last");

        Person foundPerson = foundPersons.get(0);

        assertEquals("first last", foundPerson.getFirstName() + " " + foundPerson.getLastName());
    }
    @Test
    public void searchEvents()
    {
        ArrayList<Event> eventsToSearch = cache.getEvents();

        ArrayList<Event> foundEvents = cache.searchEvents(eventsToSearch, "first");

        assertEquals(1, foundEvents.size());
    }
    @Test
    public void searchEvents2()
    {
        ArrayList<Event> eventsToSearch = cache.getEvents();

        ArrayList<Event> foundEvents = cache.searchEvents(eventsToSearch, "last");

        assertEquals("Birth", foundEvents.get(0).getEventType());
    }

    @Test
    public void filterEvents()
    {
        ArrayList<Event> allEvents = cache.getEvents();

        ArrayList<Event> filteredEvents = cache.filterEvents(true,true,true,false);

        for (Event event : filteredEvents)
        {
            Person person = cache.getPersonByID(event.getPersonID());
            assertEquals("m", person.getGender());
        }

    }
    @Test
    public void filterEvents2()
    {
        ArrayList<Event> allEvents = cache.getEvents();

        ArrayList<Event> filteredEvents = cache.filterEvents(true,true,false,true);

        for (Event event : filteredEvents)
        {
            Person person = cache.getPersonByID(event.getPersonID());
            assertEquals("f", person.getGender());
        }
    }
}
