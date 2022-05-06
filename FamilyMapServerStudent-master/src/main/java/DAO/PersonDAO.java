package DAO;

import Model.Person;
import Result.PersonResult;
import Result.PersonsResult;

import java.sql.*;

public class PersonDAO {
    private final Connection conn;

    /**
     * Creates a personDAO
     * @param conn

     */
    public PersonDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(Person person) throws DataAccessException
    {
        //
        // This method inserts a given Person model object into the Database
        //
        String sql = "INSERT INTO Persons (PersonID, AssociatedUsername, FirstName, " +
                "LastName, Gender, FatherID, MotherID, SpouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            //question mark found in our sql String
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public void addParentsToPerson(String personID, String fatherID, String motherID) throws DataAccessException
    {
        //
        // This method updates a given persons father and mother ID's.
        //
        String sql = "UPDATE Persons SET FatherID = \'" + fatherID + "\', MotherID = \'" + motherID + "\' WHERE PersonID = \'" + personID + "\';";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public Person find(String personID) throws DataAccessException
    {
        //
        // This finds and returns a person model object constructed from data in the database. if a row isnt found with matching personID, null is returned.
        //
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                         rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
                return person;
            }
        }
        catch (SQLException p)
        {
            p.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException p)
                {
                    p.printStackTrace();
                }
            }

        }
        return null;
    }

    public Person find(String personID, String username) throws DataAccessException
    {
        //
        // finds and returns a row from the DB with matching PersonID and AssociatedUsername data points. If nothing is found, we return null.
        //
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ? AND AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, personID);
            stmt.setString(2, username);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
                return person;
            }
        }
        catch (SQLException p)
        {
            p.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException p)
                {
                    p.printStackTrace();
                }
            }

        }
        return null;
    }

    public PersonsResult getAllPeople(String username) throws DataAccessException
    {

        //
        // Selects all rows with people who have the associated username passed in as a variable
        //
        String sql = "SELECT * FROM Persons WHERE AssociatedUsername = '" + username + "';";
        PersonsResult personList = new PersonsResult();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                Person person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));

                personList.add(person);
            }
            if (personList.getData().size() == 0)
            {
                return null;
            }
            return personList;

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while querying the database");
        }
    }

    /**
     * clears the People table in DB
     * @return
     */
    public void clear() throws DataAccessException
    {
        //
        // clears all people from the Persons table
        //
        String sql = "DELETE FROM Persons;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.executeUpdate();
        }
        catch (SQLException u)
        {
            u.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Persons");
        }
    }
}
