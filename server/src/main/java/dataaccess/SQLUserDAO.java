package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    private final DatabaseManager manager;
    private Connection conn;

    public SQLUserDAO() throws DataAccessException {
        manager = new DatabaseManager();
        conn = manager.configureDatabase();
    }
    @Override
    public void createUser(UserData data) {
        String statement = "INSERT INTO userData (username, password, email) VALUES (?,?,?)";
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            String hashPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
            preparedStatement.setString(1, data.username());
            preparedStatement.setString(2, hashPassword);
            preparedStatement.setString(3, data.email());

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        String statement = "SELECT username, password, email FROM userData WHERE username =?";
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, username);

            var response = preparedStatement.executeQuery();
            String name = null;
            String password = null;
            String email = null;
            while (response.next()) {
                name = response.getString("username");
                password = response.getString("password");
                email = response.getString("email");
            }
            if (name == null && password == null && email == null) {
                return null;
            }
            return new UserData(name, password, email);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement("DELETE FROM userData");
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
