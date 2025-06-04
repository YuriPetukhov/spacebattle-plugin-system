package org.spacebattle;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AuthClient {

    /**
     * Выполняет авторизацию и устанавливает токен в System properties.
     * @param loginUrl URL логина
     */
    public static void authenticate(String loginUrl) throws Exception {
        URL url = new URL(loginUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(0);
        }

        if (conn.getResponseCode() == 200) {
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                String json = scanner.useDelimiter("\\A").next();
                String token = json.replaceAll(".*\"token\":\\s*\"([^\"]+)\".*", "$1");
                System.setProperty("auth.token", token);
                System.out.println("Token set: " + token);
            }
        } else {
            System.err.println("Login failed: " + conn.getResponseCode());
        }
    }

    public static void main(String[] args) throws Exception {
        authenticate("http://localhost:8081/login");
    }
}
