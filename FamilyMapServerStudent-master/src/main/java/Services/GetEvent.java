package Services;

import DAO.*;
import Model.Event;
import Result.EventResult;
import Result.EventsResult;

import java.sql.Connection;

/**
 * GetEvent
 */
public class GetEvent {
    Event event;

    Database db;
    Connection conn;

    EventDAO eDAO;
    GeneralDAO gDAO;
    AuthTokenDAO aDAO;

    public GetEvent() throws DataAccessException
    {
        db = new Database("jdbc:sqlite:familymap.sqlite");
        conn = db.getConnection();

        eDAO = new EventDAO(conn);
        gDAO = new GeneralDAO(conn);
        aDAO = new AuthTokenDAO(conn);
    }

    public EventResult getEvent(String eventID, String authtoken) throws DataAccessException
    {
        try
        {
            //
            // Find an event that matches the passed in eventID, as long as it also matches the passed in authToken.
            //
            if (aDAO.findByToken(authtoken) == null)
            {
                db.closeConnection(false);
                return null;
            }
            else
            {
                String username = aDAO.findByToken(authtoken).getUsername();

                event = eDAO.find(eventID, username);

                if (event == null)
                {
                    db.closeConnection(false);
                    return null;
                }
            }
        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
        }

        EventResult toReturn = new EventResult(event);

        db.closeConnection(true);
        return toReturn;
    }
    public EventsResult getEvent(String authtoken) throws DataAccessException
    {
        EventsResult result;

        try
        {
            //
            // if we find anything to be null, this means we didnt find what we are looking for in the DB. so we return null.
            //
            if(aDAO.findByToken(authtoken) == null)
            {
                db.closeConnection(false);
                return null;
            }
            else
            {
                String username = aDAO.findByToken(authtoken).getUsername();
                result = eDAO.getAllEvents(username);
            }

        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
            throw d;
        }

        if (result == null)
        {
            db.closeConnection(false);
            return null;
        }
        else
        {
            db.closeConnection(true);
            return result;
        }

        //System.out.println(rs.getString("EventID"));
    }
}