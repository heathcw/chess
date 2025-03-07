package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final MemoryUserDAO userDataAccess;
    private final MemoryAuthDAO authDataAccess;
    private final SQLUserDAO userSQL;

    public UserService() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
        try {
            userSQL = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData data = new UserData(request.username(), request.password(), request.email());
        UserData check = userDataAccess.getUser(request.username());
        String tokenToAdd = UUID.randomUUID().toString();
        AuthData token = new AuthData(tokenToAdd, request.username());

        if (check != null) {
            throw new DataAccessException("Error: already taken");
        }

        userDataAccess.createUser(data);
        authDataAccess.createAuth(token);

        return new RegisterResult(request.username(), tokenToAdd);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData data = userDataAccess.getUser(request.username());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (!Objects.equals(request.password(), data.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String tokenToAdd = UUID.randomUUID().toString();
        AuthData token = new AuthData(tokenToAdd, request.username());
        authDataAccess.createAuth(token);

        return new LoginResult(request.username(), tokenToAdd);
    }

    public LogoutResult logout(AuthRequest request) throws DataAccessException {
        AuthData data = authDataAccess.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authDataAccess.deleteAuth(data);

        return new LogoutResult();
    }
}
