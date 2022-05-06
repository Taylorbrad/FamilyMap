package Result;

public class LoginResult {
    public String authtoken;
    public String username;
    public String personID;
    public boolean success;
    public String message;

    /**
     * creates new loginResult object
     * @param authtoken
     * @param username
     * @param personID
     * @param success
     *
     */
    public LoginResult(String authtoken, String username, String personID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
    }

    /**
     * returns the authToken
     * @return authtoken
     */
    public String getAuthtoken() {
        return authtoken;
    }

    /**
     * sets the authtoken (from DB)
     * @param authtoken
     */
    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    /**
     * returns the username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the username (from DB)
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * returns personID
     * @return personID
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * sets the personID (from DB)
     * @param personID
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * returns the success boolean, which tells us if the login attempt was successful
     * @return success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * sets the success boolean
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
