package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import server.Constants;

public class UserService {
    public AuthData register(UserData user, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        String username = user.username();
        if (userDAO.checkUser(username)) {
            throw new DataAccessException(Constants.ALREADY_TAKEN);
        }
        if (!userDAO.validPassword(user)) {
            throw new DataAccessException(Constants.UNAUTHORIZED);
        }
        userDAO.createUser(user);
        return authDAO.createAuth(username);
    }
    public AuthData login(UserData user, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        String username = user.username();
        UserData selectedUser = userDAO.getUser(username);
        if (userDAO.checkPassword(selectedUser.password(), user.password())) {
            return authDAO.createAuth(username);
        }
        throw new DataAccessException(Constants.UNAUTHORIZED);
    }

    public void logout(String authToken, AuthDAO authDAO) throws DataAccessException{
        authDAO.getAuth(authToken);
        authDAO.deleteAuth(authToken);

    }

}
