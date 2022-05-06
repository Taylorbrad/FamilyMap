package DAO;

import Model.AuthToken;

import java.sql.*;

public class AuthTokenDAO {

    private final Connection conn;

    /**
     * Creates an AuthToken DAO
     * @param conn
     */
    public AuthTokenDAO(Connection conn) {
        //this.authtoken = authtoken;
        this.conn = conn;
    }

    public void insert(AuthToken token) throws DataAccessException
    {
        String sql = "INSERT INTO Authtokens (Authtoken, Username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, token.getAuthtoken());
            stmt.setString(2, token.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public AuthToken find(String username) throws DataAccessException
    {
        AuthToken token;
        ResultSet rs = null;
        //
        //Checks the Authtokens table, and replaces the ? with the passed in 'username' variable, and pulls each row where the usernames match
        //
        String sql = "SELECT * FROM Authtokens WHERE Username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token = new AuthToken(rs.getString("Authtoken"), rs.getString("Username"));
                //
                //returns the token when the username is found in the table
                //
                return token;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        }
        finally
        {
            if(rs != null)
            {
                //If nothing is found, code gets here. Eventually, it returns null
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public AuthToken findByToken(String authToken) throws DataAccessException
    {
        //
        //Same as the above, but searches by Authtoken variable, instead of username
        //
        AuthToken token;
        ResultSet rs = null;
        String sql = "SELECT * FROM Authtokens WHERE Authtoken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, authToken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token = new AuthToken(rs.getString("Authtoken"), rs.getString("Username"));
                return token;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }
    public void clear() throws DataAccessException
    {
        //
        //clears each row from the Authtokens table
        //
        String sql = "DELETE FROM Authtokens;";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.executeUpdate();
        }
        catch (SQLException u)
        {
            u.printStackTrace();
            throw new DataAccessException("Error encountered while clearing authtoken");
        }
    }
}
