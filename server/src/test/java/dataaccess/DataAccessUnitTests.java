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

    @Test
    public void getUserTest() {
        try {
            SQLUserDAO userSQL = new SQLUserDAO();
            UserData add = new UserData("user2", "pass2", "email2");
            userSQL.createUser(add);
            assert userSQL.getUser("user2").equals(add);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedGetUserTest() {
        try {
            SQLUserDAO userSQL = new SQLUserDAO();
            UserData check = userSQL.getUser("newUser");
            UserData empty = new UserData(null, null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void clearTest() {
        try {
            SQLUserDAO userSQL = new SQLUserDAO();
            UserData add = new UserData("user3", "pass3", "email3");
            userSQL.createUser(add);
            userSQL.clear();
            UserData check = userSQL.getUser("user3");
            UserData empty = new UserData(null, null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
