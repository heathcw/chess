package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    private final DatabaseManager manager;
    private Connection conn;

    public SQLAuthDAO() throws DataAccessException {
        manager = new DatabaseManager();
        conn = manager.configureDatabase();
    }

    @Override
    public void createAuth(AuthData data) {
        String statement = "INSERT INTO authData (authToken, username) VALUES (?,?)";
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, data.authToken());
            preparedStatement.setString(2, data.username());

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT authToken, username FROM authData WHERE authToken =?";
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, authToken);

            var response = preparedStatement.executeQuery();
            String auth = null;
            String username = null;
            while (response.next()) {
                auth = response.getString("authToken");
                username = response.getString("username");
            }
            if (auth == null && username == null) {
                return null;
            }
            return new AuthData(auth, username);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(AuthData data) {
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?");
            preparedStatement.setString(1, data.authToken());
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement("DELETE FROM authData");
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
