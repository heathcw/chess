package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    public void userClearTest() {
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

    @Test
    public void createAuthTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData add = new AuthData("token", "user");
            authSQL.createAuth(add);
            assert authSQL.getAuth("token").equals(add);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedCreateAuthTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData add = new AuthData(null, "user");
            authSQL.createAuth(add);
        } catch (DataAccessException | RuntimeException e) {
            assert e.getMessage().equals("java.sql.SQLIntegrityConstraintViolationException: Column 'authToken' cannot be null");
        }
    }

    @Test
    public void getAuthTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData add = new AuthData("myToken", "myUsername");
            authSQL.createAuth(add);
            assert authSQL.getAuth("myToken").equals(add);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedGetAuthTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData check = authSQL.getAuth("empty");
            AuthData empty = new AuthData(null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteAuthTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData add = new AuthData("newToken", "newUser");
            authSQL.createAuth(add);
            authSQL.deleteAuth(add);
            AuthData check = authSQL.getAuth("newToken");
            AuthData empty = new AuthData(null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedDeleteAuthTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData delete = new AuthData(null, "user");
            authSQL.deleteAuth(delete);
        } catch (DataAccessException | RuntimeException e) {
            assert e.getMessage().equals("java.sql.SQLIntegrityConstraintViolationException: Column 'authToken' cannot be null");
        }
    }

    @Test
    public void authClearTest() {
        try {
            SQLAuthDAO authSQL = new SQLAuthDAO();
            AuthData add = new AuthData("token3", "user3");
            authSQL.createAuth(add);
            authSQL.clear();
            AuthData check = authSQL.getAuth("token3");
            AuthData empty = new AuthData(null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGameTest() {
        try {
            ChessGame game = new ChessGame();
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(1, "user1", "user2", "name", game);
            gameSQL.createGame(add);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedCreateGameTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(0, null, null, "name", null);
            gameSQL.createGame(add);
        } catch (DataAccessException | RuntimeException e) {
            assert e.getMessage().equals("java.sql.SQLIntegrityConstraintViolationException: Column 'game' cannot be null");
        }
    }

    @Test
    public void getGameByIDTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(3, "white", "black", "wvb", new ChessGame());
            gameSQL.createGame(add);
            GameData check = gameSQL.getGameByID(3);
            assert check.gameID() == add.gameID();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedGetGameByIDTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData check = gameSQL.getGameByID(20);
            GameData empty = new GameData(0, null, null, null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getGameByNameTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(5, "white", "black", "bvw", new ChessGame());
            gameSQL.createGame(add);
            GameData check = gameSQL.getGameByName("bvw");
            assert check.gameID() == add.gameID();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedGetGameByNameTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData check = gameSQL.getGameByName("empty");
            GameData empty = new GameData(0, null, null, null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void listGamesTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(101, "white1", "black1", "name1", new ChessGame());
            gameSQL.createGame(add);
            ArrayList<GameData> check = gameSQL.listGames();
            assert !check.isEmpty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void joinGameTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(102, null, null, "name3", new ChessGame());
            gameSQL.createGame(add);
            gameSQL.joinGame("WHITE", "user1", 102);
            GameData check = gameSQL.getGameByID(102);
            assert check.whiteUsername().equals("user1");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failedJoinGameTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(103, null, "taken", "name4", new ChessGame());
            gameSQL.createGame(add);
            gameSQL.joinGame("BLACK", "user1", 103);
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: already taken");
        }
    }

    @Test
    public void gameClearTest() {
        try {
            SQLGameDAO gameSQL = new SQLGameDAO();
            GameData add = new GameData(42, "black", "white", "nombre", new ChessGame());
            gameSQL.createGame(add);
            gameSQL.clear();
            GameData check = gameSQL.getGameByID(42);
            GameData empty = new GameData(0, null, null, null, null);
            assert empty.equals(check);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
