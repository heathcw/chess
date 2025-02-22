package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void createGame(GameData data);
    GameData getGame(int ID);
    ArrayList<GameData> listGames();
    void updateGame(GameData data);
}
