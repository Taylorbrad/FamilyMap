package Request;

public class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public String firstName;
    public String lastName;
    public String gender;

    /**
     * Creates new register request object
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param gender
     */
    public RegisterRequest(String username, String password, String email, String firstName, String lastName, String gender) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    /*
    @Override
    public String toString() {
        return "RegisterRequest:  {" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

     */

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }
}
