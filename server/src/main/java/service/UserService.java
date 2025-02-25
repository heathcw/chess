package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private MemoryUserDAO userDataAccess;
    private MemoryAuthDAO authDataAccess;

    public UserService() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
    }

    public RegisterResult register(RegisterRequest request) {
        UserData data = new UserData(request.username(), request.password(), request.email());
        UserData check = userDataAccess.getUser(request.username());
        String tokenToAdd = UUID.randomUUID().toString();
        AuthData token = new AuthData(tokenToAdd, request.username());

        if (check == null) {
            return null;
        }

        userDataAccess.createUser(data);
        authDataAccess.createAuth(token);

        return new RegisterResult(request.username(), tokenToAdd);
    }

    public LoginResult login(LoginRequest request) {
        UserData data = userDataAccess.getUser(request.username());
        if (data == null) {
            return null;
        }

        String tokenToAdd = UUID.randomUUID().toString();
        AuthData token = new AuthData(tokenToAdd, request.username());
        authDataAccess.createAuth(token);

        return new LoginResult(request.username(), tokenToAdd);
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        AuthData data = authDataAccess.getAuth(request.authToken());
        if (data == null) {
            return null;
        }

        authDataAccess.deleteAuth(data);

        return new LogoutResult();
    }
}
