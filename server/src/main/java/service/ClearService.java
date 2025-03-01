package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class ClearService {

    private MemoryGameDAO gameDataAccess;
    private MemoryAuthDAO authDataAccess;
    private MemoryUserDAO userDataAccess;

    public ClearService(){
        gameDataAccess = new MemoryGameDAO();
        authDataAccess = new MemoryAuthDAO();
        userDataAccess = new MemoryUserDAO();
    }

    public ClearResult delete() {
        gameDataAccess.clear();
        authDataAccess.clear();
        userDataAccess.clear();
        return new ClearResult();
    }
}
