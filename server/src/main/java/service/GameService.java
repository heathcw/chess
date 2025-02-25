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

    private MemoryGameDAO gameDataAccess;
    private MemoryAuthDAO authDataAccess;

    public GameService(){}

    public ListResult listGames(ListRequest request) throws DataAccessException {
        try {
            authDataAccess.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ArrayList<GameData> games = gameDataAccess.listGames();
        return new ListResult(games);
    }

    public GameResult createGame(GameRequest request) throws DataAccessException {
        try {
            authDataAccess.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        GameData check = gameDataAccess.getGameByName(request.gameName());
        if (check != null) {
            throw new DataAccessException("Error: Game already exists");
        }
        Random rand = new Random();
        int ID = rand.nextInt((9999 - 100) + 1) + 10;
        GameData gameToAdd = new GameData(ID, "","", request.gameName(), new ChessGame());
        gameDataAccess.createGame(gameToAdd);
        return new GameResult(ID);
    }

    public JoinResult joinGame(JoinRequest request) throws DataAccessException {
        try {
           AuthData auth = authDataAccess.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        AuthData auth = authDataAccess.getAuth(request.authToken());
        gameDataAccess.joinGame(request.playerColor(), auth.username(), request.gameID());

        return new JoinResult();
    }
}
