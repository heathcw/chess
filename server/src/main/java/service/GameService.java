package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Random;

public class GameService {

    private final MemoryGameDAO gameDataAccess;
    private final MemoryAuthDAO authDataAccess;
    private final SQLAuthDAO authSQL;
    private final SQLGameDAO gameSQL;

    public GameService(){
        gameDataAccess = new MemoryGameDAO();
        authDataAccess = new MemoryAuthDAO();
        try {
            gameSQL = new SQLGameDAO();
            authSQL = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ListResult listGames(AuthRequest request) throws DataAccessException {
        AuthData data = authSQL.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        ArrayList<GameData> games = gameSQL.listGames();
        return new ListResult(games);
    }

    public GameResult createGame(GameRequest request) throws DataAccessException {
        AuthData data = authSQL.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        GameData check = gameSQL.getGameByName(request.gameName());
        if (check != null) {
            throw new DataAccessException("Error: Game already exists");
        }
        Random rand = new Random();
        int id = rand.nextInt((9999 - 100) + 1) + 10;
        GameData gameToAdd = new GameData(id, null,null, request.gameName(), new ChessGame());
        gameSQL.createGame(gameToAdd);
        return new GameResult(id);
    }

    public JoinResult joinGame(JoinRequest request) throws DataAccessException {
        AuthData data = authSQL.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData auth = authSQL.getAuth(request.authToken());
        gameSQL.joinGame(request.playerColor(), auth.username(), request.gameID());

        return new JoinResult();
    }
}
