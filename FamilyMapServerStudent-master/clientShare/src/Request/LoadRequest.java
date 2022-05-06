package Request;

import Model.*;

import java.util.ArrayList;

public class LoadRequest {
    public ArrayList<User> users;
    public ArrayList<Person> persons;
    public ArrayList<Event> events;

    /**
     * creates new LoadRequest object
     * @param users
     * @param persons
     * @param events
     */
    public LoadRequest(ArrayList<User> users, ArrayList<Person> persons, ArrayList<Event> events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
