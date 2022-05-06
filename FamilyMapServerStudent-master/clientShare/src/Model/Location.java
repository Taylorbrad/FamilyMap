package Model;

import java.util.ArrayList;

public class Location {
    ArrayList<Location> locations;
    String country;
    String city;
    float latitude;
    float longitude;

    public Location(String country, String city, float latitude, float longitude) {
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<Location> getLocations()
    {
        return locations;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }




}
