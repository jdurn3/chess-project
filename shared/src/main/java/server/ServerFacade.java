package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.GameData;
import model.GameName;
import model.Join;
import model.UserData;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public void register(UserData user) throws DataAccessException {
        var path = "/user";
        this.makeRequest("POST", path, user, UserData.class);
    }

    public void login(String username, String password) throws DataAccessException {
        var path = "/session";
        var requestBody = new UserData(username, password, null);
        this.makeRequest("POST", path, requestBody, UserData.class);
    }
    public void logout() throws DataAccessException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }
    public void createGame(String gameName) throws DataAccessException {
        var path = "/game";
        GameName requestBody = new GameName(gameName);
        this.makeRequest("POST", path, requestBody, GameData.class);
    }

    public GameData[] listGames() throws DataAccessException {
        var path = "/game";
        record listGameResponse(GameData[] games) {
        }
        var response = this.makeRequest("GET", path, null, listGameResponse.class);
        return response.games();
    }

    public void joinGame(int gameID, ChessGame.TeamColor teamColor) throws DataAccessException {
        var path = "/game";
        Join requestBody = new Join(teamColor, gameID);
        this.makeRequest("PUT", path, requestBody, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}