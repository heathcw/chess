package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Random;

public class GameService {

    private final MemoryGameDAO gameDataAccess;
    private final MemoryAuthDAO authDataAccess;

    public GameService(){
        gameDataAccess = new MemoryGameDAO();
        authDataAccess = new MemoryAuthDAO();
    }

    public ListResult listGames(AuthRequest request) throws DataAccessException {
        AuthData data = authDataAccess.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        ArrayList<GameData> games = gameDataAccess.listGames();
        return new ListResult(games);
    }

    public GameResult createGame(GameRequest request) throws DataAccessException {
        AuthData data = authDataAccess.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        GameData check = gameDataAccess.getGameByName(request.gameName());
        if (check != null) {
            throw new DataAccessException("Error: Game already exists");
        }
        Random rand = new Random();
        int id = rand.nextInt((9999 - 100) + 1) + 10;
        GameData gameToAdd = new GameData(id, null,null, request.gameName(), new ChessGame());
        gameDataAccess.createGame(gameToAdd);
        return new GameResult(id);
    }

    public JoinResult joinGame(JoinRequest request) throws DataAccessException {
        AuthData data = authDataAccess.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData auth = authDataAccess.getAuth(request.authToken());
        gameDataAccess.joinGame(request.playerColor(), auth.username(), request.gameID());

        return new JoinResult();
    }
}
