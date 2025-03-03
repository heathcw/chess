package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    static ArrayList<GameData> games = new ArrayList<>();

    public MemoryGameDAO() {
    }

    @Override
    public void createGame(GameData data) {
        games.add(data);
    }

    @Override
    public GameData getGameByID(int id) {
        for (GameData game : games) {
            if (game.gameID() == id) {
                return game;
            }
        }
        return null;
    }

    @Override
    public GameData getGameByName(String gameName) {
        for (GameData game : games) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return games;
    }

    @Override
    public void joinGame(String playerColor, String username, int id) throws DataAccessException {
        GameData gameToJoin = getGameByID(id);
        if (gameToJoin == null) {
            throw new DataAccessException("Error: game not found");
        }
        GameData changeGame;
        if (playerColor.equals("WHITE")) {
            if (gameToJoin.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            changeGame = new GameData(id, username, gameToJoin.blackUsername(), gameToJoin.gameName(), gameToJoin.game());
        } else if (playerColor.equals("BLACK")) {
            if (gameToJoin.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            changeGame = new GameData(id, gameToJoin.whiteUsername(), username, gameToJoin.gameName(), gameToJoin.game());
        } else {
            throw new DataAccessException("Error: bad request");
        }
        games.set(games.indexOf(gameToJoin), changeGame);
    }

    @Override
    public void clear() {
        games.clear();
    }
}
