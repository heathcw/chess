package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> games = new ArrayList<>();

    public MemoryGameDAO() {}

    @Override
    public void createGame(GameData data) {
        games.add(data);
    }

    @Override
    public GameData getGame(int ID) {
        for (GameData game: games) {
            if (game.gameID() == ID) {
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
    public void updateGame(GameData data) {

    }
}
