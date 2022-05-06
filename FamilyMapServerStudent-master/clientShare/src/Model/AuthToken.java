package Model;

public class AuthToken {
    public String authtoken;
    public String username;

    /**
     * Creates an AuthToken
     * @param username
     */
    public AuthToken(String authtoken, String username) {
        this.authtoken = authtoken;
        this.username = username;
    }
    public String getAuthtoken() {
        return authtoken;
    }
    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        //
        // This compares each data point of the object to that of the passed in object. returns true only if each is equal.
        //
        if (o == null)
            return false;
        if (o instanceof AuthToken) {
            AuthToken oToken = (AuthToken) o;
            return oToken.getUsername().equals(getUsername()) &&
                    oToken.getAuthtoken().equals(getAuthtoken());
        } else {
            return false;
        }
    }

}
