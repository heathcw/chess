package service;

public class UserService {

    //records for users
    record RegisterRequest(String username, String password, String email){}
    record RegisterResult(String username, String authToken){}
    record LoginRequest(String username, String password){}
    record LoginResult(String username, String password){}
    record LogoutRequest(String authToken){}
    record LogoutResult(){}

    //functions for userService
}
