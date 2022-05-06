package DAO;

import Model.Event;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final Connection conn;

    /**
     * creates new UserDAO
     * @param conn
     */
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(User user) throws DataAccessException {
        //
        // Here we insert a given User object into the DB
        //
        String sql = "INSERT INTO Users (Username, Password, Email, FirstName, " +
                "LastName, Gender, PersonID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public User find(String username) throws DataAccessException {
        //
        // This method returns a user with the matching username, as a User object
        //
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE Username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                user = new User(rs.getString("Username"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("Gender"), rs.getString("PersonID"));
                return user;
            }
        }
        catch (SQLException u)
        {
            u.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException u)
                {
                    u.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * clears Users table from the database
     * @return
     */
    public void clear() throws DataAccessException
    {
        //
        // Clears all rows from the Users table
        //
        String sql = "DELETE FROM Users;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.executeUpdate();
        }
        catch (SQLException u)
        {
            u.printStackTrace();
            throw new DataAccessException("Error encountered while clearing user");
        }
    }
}
