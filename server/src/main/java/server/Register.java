package server;
import dataAccess.DataAccessException;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.*;
import com.google.gson.Gson;
import model.AuthData;

public class Register {
    public Object register(Request req, Response res) {
        String information = req.body();
        UserData newUser = new Gson().fromJson(information, UserData.class);
        AuthData authToken = null;
        try {
            authToken = new UserService().register(newUser);
        } catch (DataAccessException e) {
            var body = new Gson().toJson(e.getMessage());
            res.type("application/json");
            res.status(403);
            res.body(body);
            return body;
        }
        var body = new Gson().toJson(authToken);
        res.type("application/json");
        res.status(200);
        res.body(body);
        return body;
        }
    }
}
