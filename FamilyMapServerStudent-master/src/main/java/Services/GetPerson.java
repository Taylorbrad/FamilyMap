package Services;

import DAO.AuthTokenDAO;
import DAO.DataAccessException;
import DAO.Database;
import DAO.PersonDAO;
import Model.Person;
import Result.PersonResult;
import Result.PersonsResult;

import java.sql.Connection;

public class GetPerson {
    public Person person;
    Database db;
    Connection conn;
    PersonDAO pDAO;
    AuthTokenDAO aDAO;

    public GetPerson() throws DataAccessException
    {
        db = new Database("jdbc:sqlite:familymap.sqlite");
        conn = db.getConnection();

        pDAO = new PersonDAO(conn);
        aDAO = new AuthTokenDAO(conn);
    }
    public PersonResult getPerson(String personID, String authtoken) throws DataAccessException
    {
        //
        // Get a single person, based on the passed in personID, as long as it matches the passed in authtoken as well.
        //
        Person person = null;
        try
        {
            if (aDAO.findByToken(authtoken) == null)
            {
                db.closeConnection(false);
                return null;
            }
            else
            {
                String username = aDAO.findByToken(authtoken).getUsername();

                if (pDAO.find(personID, username) == null)
                {
                    db.closeConnection(false);
                    return null;
                }
                else
                {
                    person = pDAO.find(personID, username);
                }
            }

        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
            return null;
        }


        db.closeConnection(true);
        return new PersonResult(person);
        //return null;
    }

    /**
     * gets all family members of the current user
     * @return
     */
    public PersonsResult getAllPersons(String authtoken) throws DataAccessException
    {
        PersonsResult toReturn = new PersonsResult();

        if (aDAO.findByToken(authtoken) == null)
        {
            db.closeConnection(false);
            return null;
        }
        else
        {
            String associatedUsername = aDAO.findByToken(authtoken).getUsername();
            toReturn = pDAO.getAllPeople(associatedUsername);

            //
            // if any objects are null that we find, return null. This means what we are looking for doesnt exist.
            //
            if (toReturn == null)
            {
                db.closeConnection(false);
                return null;
            }
        }
        db.closeConnection(true);
        return toReturn;
    }
}
