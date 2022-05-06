package Result;

import Model.Event;

public class EventResult {

    public String eventID;
    public String associatedUsername;
    public String personID;
    public float latitude;
    public float longitude;
    public String country;
    public String city;
    public String eventType;
    public int year;
    public boolean success;
    public String message;

    /**
     * create new GetEventResult object
     * @param eventID
     * @param associatedUsername
     * @param personID
     * @param latitude
     * @param longitude
     * @param country
     * @param city
     * @param eventType
     * @param year
     */
    public EventResult(String eventID, String associatedUsername, String personID, float latitude, float longitude, String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
        this.success = true;
    }

    public EventResult(Event event)
    {
        this.eventID = event.getEventID();
        this.associatedUsername = event.getUsername();
        this.personID = event.getPersonID();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.country = event.getCountry();
        this.city = event.getCity();
        this.eventType = event.getEventType();
        this.year = event.getYear();
        this.success = true;
    }
}
