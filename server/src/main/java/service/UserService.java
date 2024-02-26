package service;

import dataAccess.AuthMemoryAccess;
import dataAccess.DataAccessException;
import dataAccess.UserMemoryAccess;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public class UserService {
    public AuthData register(UserData user, UserMemoryAccess userDAO, AuthMemoryAccess authDAO) throws DataAccessException {
        String username = user.username();
        userDAO.checkUser(username);
        userDAO.validPassword(user);
        userDAO.createUser(user);
        return authDAO.createAuth(username);
    }
    public AuthData login(UserData user, UserMemoryAccess userDAO, AuthMemoryAccess authDAO) throws DataAccessException {
        String username = user.username();
        UserData selected_user = userDAO.getUser(username);
        userDAO.checkPassword(selected_user.password(), user.password());
        return authDAO.createAuth(username);
    }

    public void logout(String authToken, AuthMemoryAccess authDAO) throws DataAccessException{
        authDAO.getAuth(authToken);
        authDAO.deleteAuth(authToken);

    }

}
