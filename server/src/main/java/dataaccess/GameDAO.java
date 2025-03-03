package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void createGame(GameData data);
    GameData getGameByID(int id);
    GameData getGameByName(String gameName);
    ArrayList<GameData> listGames();
    void joinGame(String playerColor, String username, int id) throws DataAccessException;
    void updateGame(GameData data);
    void clear();
}
