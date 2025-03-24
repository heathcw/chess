package service;

import dataaccess.*;
import model.LogoutResult;

public class ClearService {

    private final MemoryGameDAO gameDataAccess;
    private final MemoryAuthDAO authDataAccess;
    private final MemoryUserDAO userDataAccess;
    private final SQLAuthDAO authSQL;
    private final SQLGameDAO gameSQL;
    private final SQLUserDAO userSQL;

    public ClearService(){
        gameDataAccess = new MemoryGameDAO();
        authDataAccess = new MemoryAuthDAO();
        userDataAccess = new MemoryUserDAO();
        try {
            gameSQL = new SQLGameDAO();
            authSQL = new SQLAuthDAO();
            userSQL = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LogoutResult delete() {
        gameSQL.clear();
        authSQL.clear();
        userSQL.clear();
        return new LogoutResult();
    }
}
