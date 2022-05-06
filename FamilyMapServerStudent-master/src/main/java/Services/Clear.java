package Services;

import DAO.*;
import Result.ClearResult;

import java.sql.Connection;

public class Clear {
    String message;
    boolean success;
    Database db;

    /**
     * Creates new ClearResult object
     *
     */
    public Clear()
    {
        db = new Database("jdbc:sqlite:familymap.sqlite");
    }

    public ClearResult clear() throws DataAccessException
    {
        Connection conn = db.getConnection();

        AuthTokenDAO aDAO = new AuthTokenDAO(conn);
        EventDAO eDAO = new EventDAO(conn);
        PersonDAO pDAO = new PersonDAO(conn);
        UserDAO uDAO = new UserDAO(conn);

        try
        {
            //
            // attempt to clear each table. as long as nothing goes wrong, return a success message.
            //
            aDAO.clear();
            eDAO.clear();
            pDAO.clear();
            uDAO.clear();
        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
            throw d;
        }


        //TODO check if tables are actually empty

        db.closeConnection(true);

        return new ClearResult("clear succeeded", true);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
