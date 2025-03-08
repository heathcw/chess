package dataaccess;

import com.google.gson.Gson;
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
