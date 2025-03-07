package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    private final Connection conn;

    public SQLUserDAO() throws DataAccessException {
        DatabaseManager manager = new DatabaseManager();
        conn = manager.configureDatabase();
    }
    @Override
    public void createUser(UserData data) {
        String statement = "INSERT INTO userData (username, password, email) VALUES (?,?,?)";
        try {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, data.username());
            preparedStatement.setString(2, data.password());
            preparedStatement.setString(3, data.email());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        String statement = "SELECT username, password, email FROM userData WHERE username =?";
        try {
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, username);

            var response = preparedStatement.executeQuery();
            return new UserData(response.getString("username"), response.getString("password"),
                    response.getString("password"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            var preparedStatement = conn.prepareStatement("DROP TABLE userData");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
