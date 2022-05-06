package Services;

import DAO.*;
import Model.AuthToken;
import Model.Person;
import Model.User;
import Request.RegisterRequest;
import Result.RegisterResult;

import java.sql.Connection;

public class Register extends BaseService {

    public String authtoken;
    public RegisterRequest request;
    public RegisterResult result;
    private Database db;

    /**
     * creates new Register object
     * @param authtoken
     * @param request
     * @param result
     */
    public Register(String authtoken, RegisterRequest request, RegisterResult result) {
        //this.authtoken = authtoken;
        //this.request = request;
        //this.result = makeRequest(this.request);
    }

    public Register()
    {
        db = new Database("jdbc:sqlite:familymap.sqlite");
        authtoken = generateUUID();
    }

    /**
     * attempts to register user with provided RegisterRequest
     * @param inRequest
     * @return
     */
    public RegisterResult makeRequest(RegisterRequest inRequest) throws DataAccessException
    {
        Connection conn = db.getConnection();

        UserDAO uDAO = new UserDAO(conn);
        AuthTokenDAO aDAO = new AuthTokenDAO(conn);
        PersonDAO pDAO = new PersonDAO(conn);

        String newID = generateUUID();
        String newToken = generateUUID();

        String username = inRequest.getUsername();
        String password = inRequest.getPassword();
        String email = inRequest.getEmail();
        String firstName = inRequest.getFirstName();
        String lastName = inRequest.getLastName();
        String gender = inRequest.getGender();

        if (uDAO.find(username) != null)
        {
            //
            // if an object is found with the given username, return null. This is not allowed.
            //
            db.closeConnection(false);
            return null;
        }

        //create new user with data from the request, and ID generated by UUID thing
        User user = new User(username, password, email, firstName, lastName, gender, newID);
        AuthToken token = new AuthToken(newToken, username);
        Person person = new Person(newID, username, firstName, lastName, gender, null,null,null);

        try
        {
            //
            //Try inserting the data. if it passes, a result is created and returned.
            //
            uDAO.insert(user);
            aDAO.insert(token);
            pDAO.insert(person);
        }
        catch (DataAccessException d)
        {
            db.closeConnection(false);
            throw d;
        }

        RegisterResult result = new RegisterResult(newToken, username, newID, true, "New user added to database");

        db.closeConnection(true);

        return result;
        //return null; //new RegisterResult();
    }
}
