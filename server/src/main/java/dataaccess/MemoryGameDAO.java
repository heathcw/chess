package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    static ArrayList<GameData> games = new ArrayList<>();

    public MemoryGameDAO() {}

    @Override
    public void createGame(GameData data) {
        games.add(data);
    }

    @Override
    public GameData getGameByID(int ID) {
        for (GameData game: games) {
            if (game.gameID() == ID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public GameData getGameByName(String gameName) {
        for (GameData game: games) {
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
    public void joinGame(String playerColor, String username, int ID) {
        GameData gameToJoin = getGameByID(ID);
        if (playerColor.equals("WHITE")) {

        }
    }

    @Override
    public void updateGame(GameData data) {

    }
}
