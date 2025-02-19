package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData data);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData data);
}
