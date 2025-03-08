package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

public class DataAccessUnitTests {

    @Test
    public void createUserTest() {
        try {
            SQLUserDAO userSQL = new SQLUserDAO();
            UserData add = new UserData("username", "password", "email");
            userSQL.createUser(add);
            assert userSQL.getUser("username").equals(add);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedCreateUserTest() {
        try {
            SQLUserDAO userSQL = new SQLUserDAO();
            UserData add = new UserData("username", "password", null);
            userSQL.createUser(add);
        } catch (DataAccessException | RuntimeException e) {
            assert e.getMessage().equals("java.sql.SQLIntegrityConstraintViolationException: Column 'email' cannot be null");
        }
    }
}
