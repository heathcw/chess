package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.Test;

public class ServiceUnitTests {

    @Test
    public void registerTest() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest request = new RegisterRequest("me","","");
        RegisterResult result = service.register(request);

        assert result.username().equals("me");
    }

    @Test
    public void failedRegisterTest() {
        UserService service = new UserService();
        RegisterRequest request = new RegisterRequest("new","old","1");
        RegisterRequest request2 = new RegisterRequest("new","old","1");
        try {
            service.register(request);
            service.register(request2);
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: already taken");
        }
    }

    @Test
    public void loginTest() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest register = new RegisterRequest("they", "you", "we");
        service.register(register);
        LoginRequest login = new LoginRequest("they","you");
        LoginResult result = service.login(login);

        assert result.username().equals("they");
    }

    @Test
    public void failedLoginTest() {
        UserService service = new UserService();
        LoginRequest login = new LoginRequest("you", "me");
        try {
            service.login(login);
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: unauthorized");
        }
    }

    @Test
    public void logoutTest() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest register = new RegisterRequest("1", "2", "3");
        service.register(register);
        LoginRequest login = new LoginRequest("1","2");
        LoginResult result = service.login(login);
        String auth = result.authToken();
        AuthRequest logout = new AuthRequest(auth);
        LogoutResult check = service.logout(logout);

        assert check != null;
    }

    @Test
    public void failedLogoutTest() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest register = new RegisterRequest("username", "password", "55");
        service.register(register);
        LoginRequest login = new LoginRequest("username","password");
        service.login(login);
        AuthRequest logout = new AuthRequest("1234");
        try {
            service.logout(logout);
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: unauthorized");
        }
    }

    @Test
    public void createGameTest() throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.delete();
        UserService userService = new UserService();
        RegisterRequest register = new RegisterRequest("4", "5", "6");
        userService.register(register);
        LoginRequest login = new LoginRequest("4","5");
        LoginResult loginResult = userService.login(login);
        String auth = loginResult.authToken();
        GameService gameService = new GameService();
        GameRequest request = new GameRequest("name", auth);
        GameResult check = gameService.createGame(request);

        assert check != null;
    }

    @Test
    public void failedCreateGameTest() throws DataAccessException {
        UserService userService = new UserService();
        RegisterRequest register = new RegisterRequest("username1", "password1", "66");
        userService.register(register);
        LoginRequest login = new LoginRequest("username1","password1");
        LoginResult loginResult = userService.login(login);
        String auth = loginResult.authToken();
        GameService gameService = new GameService();
        GameRequest request = new GameRequest("name", auth);
        try {
            gameService.createGame(request);
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: already exists");
        }
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        UserService userService = new UserService();
        RegisterRequest register = new RegisterRequest("7", "8", "9");
        userService.register(register);
        LoginRequest login = new LoginRequest("7","8");
        LoginResult loginResult = userService.login(login);
        String auth = loginResult.authToken();
        GameService gameService = new GameService();
        GameRequest request = new GameRequest("newGame", auth);
        gameService.createGame(request);
        AuthRequest list = new AuthRequest(auth);
        ListResult check = gameService.listGames(list);

        assert !check.games().isEmpty();
    }

    @Test
    public void failedListGamesTest() {
        try {
            GameService gameService = new GameService();
            gameService.listGames(new AuthRequest("1234"));
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: unauthorized");
        }
    }

    @Test
    public void joinGameTest() throws DataAccessException {
        UserService userService = new UserService();
        RegisterRequest register = new RegisterRequest("10", "11", "12");
        userService.register(register);
        LoginRequest login = new LoginRequest("10","11");
        LoginResult loginResult = userService.login(login);
        String auth = loginResult.authToken();
        GameService gameService = new GameService();
        GameRequest request = new GameRequest("myGame", auth);
        GameResult game = gameService.createGame(request);
        JoinRequest join = new JoinRequest("BLACK", game.gameID(), auth);
        JoinResult check = gameService.joinGame(join);
        AuthRequest list = new AuthRequest(auth);
        ListResult gameList = gameService.listGames(list);
        loginAndJoinTest(game.gameID());

        assert check != null && !gameList.games().isEmpty();
    }

    @Test
    public void failedJoinGameTest() {
        try {
            ClearService clearService = new ClearService();
            clearService.delete();
            UserService userService = new UserService();
            RegisterRequest register = new RegisterRequest("username2", "password2", "121");
            userService.register(register);
            LoginRequest login = new LoginRequest("username2","password2");
            LoginResult loginResult = userService.login(login);
            String auth = loginResult.authToken();
            GameService gameService = new GameService();
            JoinRequest join = new JoinRequest("BLACK", 1, auth);
            gameService.joinGame(join);
        } catch (DataAccessException e) {
            assert e.getMessage().equals("Error: game not found");
        }
    }

    public void loginAndJoinTest(int id) throws DataAccessException{
        UserService userService = new UserService();
        GameService gameService = new GameService();
        LoginRequest login = new LoginRequest("1","2");
        LoginResult result = userService.login(login);
        String auth = result.authToken();
        JoinRequest join = new JoinRequest("WHITE", id, auth);
        gameService.joinGame(join);
        AuthRequest list = new AuthRequest(auth);
        gameService.listGames(list);
    }

    @Test
    public void clearTest() {
        MemoryUserDAO users = new MemoryUserDAO();
        MemoryAuthDAO auths = new MemoryAuthDAO();
        MemoryGameDAO games = new MemoryGameDAO();

        UserData user = new UserData("","","");
        AuthData auth = new AuthData("", "");
        GameData game = new GameData(0, "", "", "", new ChessGame());

        users.createUser(user);
        auths.createAuth(auth);
        games.createGame(game);

        ClearService clear = new ClearService();
        clear.delete();
    }
}
