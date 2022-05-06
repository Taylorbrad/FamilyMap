package Services;

import DAO.AuthTokenDAO;
import DAO.DataAccessException;
import DAO.Database;
import DAO.UserDAO;
import Model.AuthToken;
import Model.User;
import Request.LoginRequest;
import Result.LoginResult;

import java.sql.Connection;

public class Login {
    Database db;

    public Login()
    {
        db = new Database("jdbc:sqlite:familymap.sqlite");
    }

    /**
     * @return Returns a new 'authtoken' (string), associated with the login instance
     */
    public LoginResult login(LoginRequest inRequest) throws DataAccessException
    {
        Connection conn = db.getConnection();

        UserDAO uDAO = new UserDAO(conn);
        AuthTokenDAO aDAO = new AuthTokenDAO(conn);

        try
        {
            User foundUser = uDAO.find(inRequest.getUsername());
            AuthToken foundToken = aDAO.find(inRequest.getUsername());

            if (foundUser != null && foundToken != null)
            {
                //
                // if this does find a matching username and password, we create
                //  a new login result with the success info and return it
                //
                String userPass = foundUser.getPassword();
                String requestPass = inRequest.getPassword();

                if (userPass.equals(requestPass))
                {
                    db.closeConnection(true);
                    return new LoginResult(foundToken.getAuthtoken(),foundUser.getUsername(), foundUser.personID, true);
                }
            }


        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
            throw d;
        }

        db.closeConnection(false);
        return null;
    }

}
