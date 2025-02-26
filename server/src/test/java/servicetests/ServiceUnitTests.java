package servicetests;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.*;

public class ServiceUnitTests {

    @Test
    public void registerTest() {
        UserService service = new UserService();
        RegisterRequest request = new RegisterRequest("me","","");
        RegisterResult result = service.register(request);

        assert result.username().equals("me");
    }

    @Test
    public void loginTest() {
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
        LoginResult result = service.login(login);

        assert result == null;
    }

    @Test
    public void logoutTest() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest register = new RegisterRequest("1", "2", "3");
        service.register(register);
        LoginRequest login = new LoginRequest("1","2");
        LoginResult result = service.login(login);
        String auth = result.authToken();
        LogoutRequest logout = new LogoutRequest(auth);
        LogoutResult check = service.logout(logout);

        assert check != null;
    }

    @Test
    public void createGameTest() throws DataAccessException {
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
        ListRequest list = new ListRequest(auth);
        ListResult check = gameService.listGames(list);

        assert !check.games().isEmpty();
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
        ListRequest list = new ListRequest(auth);
        ListResult gameList = gameService.listGames(list);


        assert check != null && !gameList.games().isEmpty();
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
