package Services;

import DAO.*;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Model.User;
import Request.LoadRequest;
import Result.LoadResult;

import java.sql.Connection;
import java.util.ArrayList;

public class Load extends BaseService{
    public LoadRequest request;

    private Database db;
    private Connection conn;

    private ArrayList<User> usersToAdd;
    private ArrayList<Event> eventsToAdd;
    private ArrayList<Person> personsToAdd;

    UserDAO uDAO;
    EventDAO eDAO;
    PersonDAO pDAO;
    AuthTokenDAO aDAO;



    public Load() throws DataAccessException
    {
        db = new Database("jdbc:sqlite:familymap.sqlite");
        conn = db.getConnection();

        uDAO = new UserDAO(conn);
        eDAO = new EventDAO(conn);
        pDAO = new PersonDAO(conn);
        aDAO = new AuthTokenDAO(conn);
    }

    /**
     * uses the LoadRequest object to send the load request, and gets a LoadResult in return.
     * @return LoadResult
     */
    public LoadResult load(LoadRequest inRequest) throws DataAccessException
    {
        new Clear().clear();

        usersToAdd = inRequest.getUsers();
        personsToAdd = inRequest.getPersons();
        eventsToAdd = inRequest.getEvents();

        int x = 0;
        int y = 0;
        int z = 0;

        //
        // keep track of x, y, and z. this tells us how many users, events and persons we add.
        //
        try
        {
            //
            // for each user, event, and person, loop through the list and insert them into the database.
            //
            for (User user : usersToAdd)
            {
                if (uDAO.find(user.getUsername()) != null)
                {
                    db.closeConnection(false);
                    return null;
                }
                AuthToken token = new AuthToken(generateUUID(), user.getUsername());
                aDAO.insert(token);
                uDAO.insert(user);
                x++;
            }
            for (Event event : eventsToAdd)
            {
                if (eDAO.find(event.getEventID()) != null)
                {
                    db.closeConnection(false);
                    return null;
                }
                eDAO.insert(event);
                y++;
            }
            for (Person person : personsToAdd)
            {
                if (pDAO.find(person.getPersonID()) != null)
                {
                    db.closeConnection(false);
                    return null;
                }
                pDAO.insert(person);
                z++;
            }
        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
            throw d;
        }
        //
        // as long as no errors occur, return the result.
        //
        db.closeConnection(true);
        return new LoadResult("Successfully added " + x + " users, " + z + " persons, and " + y + " events to the database.", true);
    }
}
