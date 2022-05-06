package Result;

import Model.Person;

public class PersonResult {

    public String associatedUsername;
    public String personID;
    public String firstName;
    public String lastName;
    public String gender;
    public String fatherID;
    public String motherID;
    public String spouseID;

    /**
     * creates new GetPersonResult object
     * @param associatedUsername
     * @param firstName
     * @param lastName
     * @param gender
     * @param fatherID
     * @param motherID
     * @param spouseID
     */
    public PersonResult(String associatedUsername, String firstName, String lastName, String gender, String fatherID, String motherID, String spouseID) {
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public PersonResult(Person person)
    {
        this.associatedUsername = person.getAssociatedUsername();
        this.personID = person.getPersonID();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.gender = person.getGender();
        this.fatherID = person.getFatherID();
        this.motherID = person.getMotherID();
        this.spouseID = person.getSpouseID();
    }
}
