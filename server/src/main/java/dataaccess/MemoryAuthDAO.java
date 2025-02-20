package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {

    private ArrayList<AuthData> authTokens = new ArrayList<>();

    public MemoryAuthDAO() {}

    @Override
    public void createAuth(AuthData data) {
        authTokens.add(data);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData token: authTokens) {
            if (token.authToken().equals(authToken)) {
                return token;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData data) {
        authTokens.remove(data);
    }
}
