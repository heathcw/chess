package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class ClearService {

    private final MemoryGameDAO gameDataAccess;
    private final MemoryAuthDAO authDataAccess;
    private final MemoryUserDAO userDataAccess;

    public ClearService(){
        gameDataAccess = new MemoryGameDAO();
        authDataAccess = new MemoryAuthDAO();
        userDataAccess = new MemoryUserDAO();
    }

    public LogoutResult delete() {
        gameDataAccess.clear();
        authDataAccess.clear();
        userDataAccess.clear();
        return new LogoutResult();
    }
}
