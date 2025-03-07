package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    private final Connection conn;

    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager manager = new DatabaseManager();
        conn = manager.configureDatabase();
    }

    @Override
    public void createAuth(AuthData data) {
        String statement = "INSERT INTO authData (authToken, username) VALUES (?,?)";
        try {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, data.authToken());
            preparedStatement.setString(2, data.username());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT authToken, username FROM authData WHERE authToken =?";
        try {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authToken);

            var response = preparedStatement.executeQuery();
            return new AuthData(response.getString("authToken"), response.getString("username"));
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(AuthData data) {
        try (var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?")) {
            preparedStatement.setString(1, data.authToken());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            var preparedStatement = conn.prepareStatement("DROP TABLE authData");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
