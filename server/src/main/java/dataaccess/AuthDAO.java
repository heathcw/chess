package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData data);
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData data);
    void clear();
}
