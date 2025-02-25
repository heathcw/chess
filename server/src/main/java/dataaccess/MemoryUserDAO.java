package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    static ArrayList<UserData> users = new ArrayList<>();

    public MemoryUserDAO() {}

    public void createUser(UserData data) {
        users.add(data);
    }

    public UserData getUser(String username) {
        for (UserData user: users) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        users.clear();
    }
}
