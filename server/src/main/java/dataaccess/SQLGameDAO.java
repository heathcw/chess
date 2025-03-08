package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    @Override
    public void createGame(GameData data) {

    }

    @Override
    public GameData getGameByID(int id) {
        return null;
    }

    @Override
    public GameData getGameByName(String gameName) {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void joinGame(String playerColor, String username, int id) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
