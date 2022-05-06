package Result;

public class RegisterResult {
    public String authtoken;
    public String username;
    public String personID;
    public boolean success;
    public String message;

    /**
     * creates new RegisterResult object
     * @param authtoken
     * @param username
     * @param personID
     * @param success
     * @param message
     */

    public RegisterResult(String authtoken, String username, String personID, boolean success, String message) {
        //request stuff here
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
        this.message = message;
    }
    public RegisterResult(String errorMessage)
    {
        this.message = errorMessage;
        this.success = false;
    }
}