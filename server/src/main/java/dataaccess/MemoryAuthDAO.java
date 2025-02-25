package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {

    static ArrayList<AuthData> authTokens = new ArrayList<>();

    public MemoryAuthDAO() {}

    @Override
    public void createAuth(AuthData data) {
        authTokens.add(data);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData token: authTokens) {
            if (token.authToken().equals(authToken)) {
                return token;
            }
        }
        throw new DataAccessException("Error: authToken does not exist");
    }

    @Override
    public void deleteAuth(AuthData data) {
        authTokens.remove(data);
    }
}
