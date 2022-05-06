package Client;

import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import Model.Event;
import Model.Person;

public class DataCache {
    private static DataCache instance = new DataCache();

    public static DataCache getInstance() { return instance; }

    private DataCache() { }


    String authToken = "";
    String personID = "";
    String firstName = "";
    String lastName = "";
    String motherID = "";
    String fatherID = "";
    ArrayList<Event> events;
    ArrayList<Event> filteredEvents;
    ArrayList<Event> outEvents = new ArrayList<>();
    ArrayList<Event> motherSideEvents = new ArrayList<>();
    ArrayList<Event> fatherSideEvents = new ArrayList<>();
    ArrayList<String> motherSideIDs = new ArrayList<>();
    ArrayList<String> fatherSideIDs = new ArrayList<>();
    ArrayList<Person> persons;
    HashSet<Person> familyMembers = new HashSet<>();
    HashSet<Person> filteredPersons = new HashSet<>();
    HashMap<String, Event> spouseBirthMap = new HashMap<>();
    HashMap<String, Person> personsByID = new HashMap<>();
    HashMap<String, ArrayList<Event>> personLifeEventsMap = new HashMap<>();

    Event eventCentered;
    Person personCentered;

    public void clearFamilyMembers()
    {
        this.familyMembers = new HashSet<>();
    }

    public ArrayList<Person> getPersonsFamilyMembersByID(String personID)
    {
        for(Person person : this.persons)
        {
            if (person.getFatherID() != null && person.getMotherID() != null)
            {
                if(person.getFatherID().equals(personID) || person.getMotherID().equals(personID))
                {
                    person.setAssociatedUsername("Child");
                    familyMembers.add(person);
                }
            }
        }

        if (getPersonByID(getPersonByID(personID).getSpouseID()) != null)
        {
            Person spouse = getPersonByID(getPersonByID(personID).getSpouseID());
            spouse.setAssociatedUsername("Spouse");
            familyMembers.add(spouse);
        }


        Person child = getPersonByID(personID);
        for (Person person : persons)
        {
            if (child.getFatherID() != null && child.getMotherID() != null)
            {
                if (person.getPersonID().equals(child.getFatherID()))
                {
                    person.setAssociatedUsername("Father");
                    familyMembers.add(person);
//                    setParents(person.getPersonID());
                }
                else if (person.getPersonID().equals(child.getMotherID()))
                {
                    person.setAssociatedUsername("Mother");
                    familyMembers.add(person);
                }
            }

        }
//        setParents(personID);

        ArrayList<Person> toReturn = new ArrayList<>();

        for (Person person : familyMembers)
        {
            toReturn.add(person);
        }

        return toReturn;
    }

    public void setEventCentered(Event eventCentered) {
        this.eventCentered = eventCentered;
    }

    public Event getEventCentered() {
        return eventCentered;
    }

    public Person getPersonCentered() {
        return personCentered;
    }

    public void setPersonCentered(Person personCentered) {
        this.personCentered = personCentered;
    }

    public void setAuthToken(String inAuth)
    {
        this.authToken=inAuth;
    }
    public String getAuthToken()
    {
        return this.authToken;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID)
    {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> searchEvents(ArrayList<Event> toSearch, String searchString)
    {
        ArrayList eventsToDisplay = new ArrayList();

        for (Event event : toSearch)
        {
            Person person = getPersonByID(event.getPersonID());

            if (event.getEventType().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)) || event.getCity().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT))
                    || event.getCountry().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)) || String.valueOf(event.getYear()).toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT))
                    || person.getLastName().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)) || person.getFirstName().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)))
            {
                eventsToDisplay.add(event);
            }
        }
        return eventsToDisplay;
    }

    public ArrayList<Person> searchPersons(ArrayList<Person> toSearch, String searchString)
    {
        ArrayList<Person> personsToDisplay = new ArrayList<>();
        for (Person person : toSearch)
        {
            if (person.getFirstName().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)) || person.getLastName().toLowerCase(Locale.ROOT).contains(searchString.toLowerCase(Locale.ROOT)))
            {
                personsToDisplay.add(person);
            }
        }

        return personsToDisplay;
    }

    public ArrayList<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(ArrayList<Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }

    public ArrayList<Event> filterEvents(boolean fatherSide, boolean motherSide, boolean maleEvents, boolean femaleEvents)
    {
        outEvents.clear();
        ArrayList<Event> toReturn = new ArrayList<>();

        if (fatherSide)
        {
            setFamilySideEvents(getPersonByID(personID).getFatherID());
        }
        if (motherSide)
        {
            setFamilySideEvents(getPersonByID(personID).getMotherID());
        }

        for (Event event : outEvents)
        {
            Person person = getPersonByID(event.getPersonID());

            if (maleEvents)
            {
                if (person.getGender().equals("m"))
                {
                    toReturn.add(event);
                }
            }
            if (femaleEvents)
            {
                if (person.getGender().equals("f"))
                {
                    toReturn.add(event);
                }
            }
        }

        return toReturn;
    }
    public void setFamilySideEvents(String personID)
    {
        Person person = getPersonByID(personID);

        ArrayList<Event> lifeEvents = getPersonLifeEventsByID().get(personID);

        for (Event event : lifeEvents)
        {
            outEvents.add(event);
        }
        if (person.getMotherID() != null)
        {
            setFamilySideEvents(person.getMotherID());
        }
        if (person.getFatherID() != null)
        {
            setFamilySideEvents(person.getFatherID());
        }

    }

    public Person getPersonByID(String personID) {
        return personsByID.get(personID);
    }

    public void setPersonsByID(HashMap<String, Person> personsByID) {
        this.personsByID = personsByID;
    }

    public HashMap<String, Person> getPersonsByID() {
        return personsByID;
    }

    public void setPersonLifeEventsMap(HashMap<String, ArrayList<Event>> personLifeEventsMap) {
        this.personLifeEventsMap = personLifeEventsMap;
    }

    public HashMap<String, ArrayList<Event>> getPersonLifeEventsByID()
    {
        return personLifeEventsMap;
    }

    public void setSpouseBirthMap(HashMap<String, Event> spouseBirthMap) {
        this.spouseBirthMap = spouseBirthMap;
    }

    public Event getSpouseBirthEventByID(String personID)
    {
        return this.spouseBirthMap.get(personID);
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public void setMotherSideIDs(String motherID)
    {
        String newFatherID = personsByID.get(motherID).getFatherID();
        String newMotherID = personsByID.get(motherID).getMotherID();

        if (newFatherID != null)
        {
            motherSideIDs.add(newFatherID);
            setMotherSideIDs(newFatherID);

        }
        if (newMotherID != null)
        {
            motherSideIDs.add(newMotherID);
            setMotherSideIDs(newMotherID);
        }
    }
    public void setFatherSideIDs(String fatherID)
    {
        String newFatherID = personsByID.get(fatherID).getFatherID();
        String newMotherID = personsByID.get(fatherID).getMotherID();

        if (newFatherID != null)
        {
            fatherSideIDs.add(newFatherID);
            setFatherSideIDs(newFatherID);
        }
        if (newMotherID != null)
        {
            fatherSideIDs.add(newMotherID);
            setFatherSideIDs(newMotherID);
        }
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public HashSet<Person> getFilteredPersons() {
        return filteredPersons;
    }

    public void setFilteredPersons(HashSet<Person> filteredPersons) {
        this.filteredPersons = filteredPersons;
    }

    public ArrayList<Event> getMotherSideEvents() {
        return motherSideEvents;
    }

    public ArrayList<Event> getFatherSideEvents() {
        return fatherSideEvents;
    }

    public ArrayList<String> getMotherSideIDs() {
        return motherSideIDs;
    }

    public ArrayList<String> getFatherSideIDs() {
        return fatherSideIDs;
    }

    public void clear()
    {
        this.authToken = "";
        this.personID = "";
        this.firstName = "";
        this.lastName = "";
        this.motherID = "";
        this.fatherID = "";
        this.persons = new ArrayList<>();
        this.events = new ArrayList<>();
        this.motherSideEvents = new ArrayList<>();
        this.fatherSideEvents = new ArrayList<>();
        this.motherSideIDs = new ArrayList<>();
        this.fatherSideIDs = new ArrayList<>();
        this.persons = new ArrayList<>();
        this.spouseBirthMap = new HashMap<>();
        this.personsByID = new HashMap<>();
        this.personLifeEventsMap = new HashMap<>();
    }

//    public void insertPersonIntoMap(Person person)
//    {
//        this.personsByID.put(person.getPersonID(), person);
//    }

}
