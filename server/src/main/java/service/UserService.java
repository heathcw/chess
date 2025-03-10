package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final MemoryUserDAO userDataAccess;
    private final MemoryAuthDAO authDataAccess;
    private final SQLUserDAO userSQL;
    private final SQLAuthDAO authSQL;

    public UserService() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
        try {
            userSQL = new SQLUserDAO();
            authSQL = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData data = new UserData(request.username(), request.password(), request.email());
        UserData check = userSQL.getUser(request.username());
        String tokenToAdd = UUID.randomUUID().toString();
        AuthData token = new AuthData(tokenToAdd, request.username());

        if (check != null) {
            throw new DataAccessException("Error: already taken");
        }

        userSQL.createUser(data);
        authSQL.createAuth(token);

        return new RegisterResult(request.username(), tokenToAdd);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData data = userSQL.getUser(request.username());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (!BCrypt.checkpw(request.password(), data.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String tokenToAdd = UUID.randomUUID().toString();
        AuthData token = new AuthData(tokenToAdd, request.username());
        authSQL.createAuth(token);

        return new LoginResult(request.username(), tokenToAdd);
    }

    public LogoutResult logout(AuthRequest request) throws DataAccessException {
        AuthData data = authSQL.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authSQL.deleteAuth(data);

        return new LogoutResult();
    }
}
