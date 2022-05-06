package DAO;

import java.sql.*;

import Model.Event;
import Result.EventResult;
import Result.EventsResult;


public class EventDAO {

    private final Connection conn;

    /**
     * creates an event DAO
     * @param conn
     */
    public EventDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(Event event) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, (float) event.getLatitude());
            stmt.setFloat(5, (float) event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        //
        // Checks the Events table, and replaces the ? with the passed in 'eventID' variable, and pulls each row where they match
        //
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                //
                // When found, a new Event Model object is constructed and returned.
                //
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public Event find(String eventID, String username) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        //
        // Selects all rows where the eventID and username values match with the passed in variables match the rows in the DB
        //
        String sql = "SELECT * FROM Events WHERE EventID = ? AND AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            stmt.setString(2, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        //
        // returns null if nothing is found
        //
        return null;
    }

    public EventsResult getAllEvents(String username) throws DataAccessException {

        String sql = "SELECT * FROM Events WHERE AssociatedUsername = \"" + username + "\";";

        //
        // Selects all events in the DB that have the matching username.
        //
        EventsResult eventList = new EventsResult();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                Event event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                //
                // We construct a new event object, and add it to the list every time we find one.
                //
                eventList.add(event);
            }
            if (eventList.getData().size() == 0)
            {
                //
                // if empty, we return null. otherwise, we return the filled list
                //
                return null;
            }
            return eventList;

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while querying the database");
        }
    }

    public void clear() throws DataAccessException
    {
        //
        // clears all rows from the events table
        //
        String sql = "DELETE FROM Events;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.executeUpdate();
        }
        catch (SQLException u)
        {
            u.printStackTrace();
            throw new DataAccessException("Error encountered while clearing event");
        }
    }
}
