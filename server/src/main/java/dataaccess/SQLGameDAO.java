package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    private final DatabaseManager manager;
    private Connection conn;

    public SQLGameDAO() throws DataAccessException {
        manager = new DatabaseManager();
        conn = manager.configureDatabase();
    }

    @Override
    public void createGame(GameData data) {
        String statement = "INSERT INTO gameData (id, whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?,?)";
        try {
            var serializer = new Gson();
            var game = data.game();
            var json = serializer.toJson(game);
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setInt(1, data.gameID());
            preparedStatement.setString(2, data.whiteUsername());
            preparedStatement.setString(3, data.blackUsername());
            preparedStatement.setString(4, data.gameName());

            preparedStatement.setString(5, json);

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGameByID(int id) {
        String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM gameData WHERE id =?";
        try {
            var serializer = new Gson();
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setInt(1, id);

            var response = preparedStatement.executeQuery();
            int gameID = 0;
            String whiteUsername = null;
            String blackUsername = null;
            String gameName = null;
            ChessGame game = null;
            String json = null;
            while (response.next()) {
                gameID = response.getInt("id");
                whiteUsername = response.getString("whiteUsername");
                blackUsername = response.getString("blackUsername");
                gameName = response.getString("gameName");
                json = response.getString("game");
            }
            if (json != null) {
                game = serializer.fromJson(json, ChessGame.class);
            }
            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGameByName(String gameName) {
        String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameName =?";
        try {
            var serializer = new Gson();
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, gameName);

            var response = preparedStatement.executeQuery();
            int gameID = 0;
            String whiteUsername = null;
            String blackUsername = null;
            String name = null;
            ChessGame game = null;
            String json = null;
            while (response.next()) {
                gameID = response.getInt("id");
                whiteUsername = response.getString("whiteUsername");
                blackUsername = response.getString("blackUsername");
                name = response.getString("gameName");
                json = response.getString("game");
            }
            if (json != null) {
                game = serializer.fromJson(json, ChessGame.class);
            }
            return new GameData(gameID, whiteUsername, blackUsername, name, game);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> returnList = new ArrayList<>();
        String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM gameData";
        try {
            var serializer = new Gson();
            conn = manager.getConnection();
            var preparedStatement = conn.prepareStatement(statement);

            var response = preparedStatement.executeQuery();
            while (response.next()) {
                int gameID = response.getInt("id");
                String whiteUsername = response.getString("whiteUsername");
                String blackUsername = response.getString("blackUsername");
                String name = response.getString("gameName");
                String json = response.getString("game");
                ChessGame game = serializer.fromJson(json, ChessGame.class);
                returnList.add(new GameData(gameID, whiteUsername, blackUsername, name, game));
            }
            return returnList;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinGame(String playerColor, String username, int id) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
