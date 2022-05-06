package DAO;

import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeneralDAO {
    private final Connection conn;

    /**
     * creates new UserDAO
     * @param conn
     */
    public GeneralDAO(Connection conn) {
        this.conn = conn;
    }

    public void customUpdateSQL(String sql) throws DataAccessException
    {
        //
        // This executes a custom UPDATE SQL command, passed in as a string.
        //
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }
}
