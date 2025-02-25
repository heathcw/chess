package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class ClearService {

    private MemoryGameDAO gameDataAccess;
    private MemoryAuthDAO authDataAccess;
    private MemoryUserDAO userDataAccess;

    public ClearService(){}

    public void delete() {
        gameDataAccess.clear();
        authDataAccess.clear();
        userDataAccess.clear();
    }
}
