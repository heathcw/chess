package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void createGame(String gameName);
    GameData getGame();
    ArrayList<GameData> listGames();
    void updateGame(GameData data);
}
