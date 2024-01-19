package services.http.clients;

import services.managers.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String apiToken;

    public KVTaskClient(String url) {
        this.url = url;
        this.apiToken = getApiToken();
    }

    private String getApiToken() {
        URI uri = URI.create(url + "register");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new ManagerSaveException(
                        "Ошибка запроса API-токена у сервера. Код ошибки: " + response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка запроса API-токена у сервера.");
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200)
                throw new ManagerSaveException(
                        "Ошибка сохранения значения по ключу " + key + ". Код ошибки: " + response.statusCode());
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка сохранения значения по ключу " + key);
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return null;
            } else if (response.statusCode() != 200)
                throw new ManagerSaveException(
                        "Ошибка загрузки значения по ключу " + key + ". Код ошибки: " + response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка загрузки значения по ключу " + key);
        }
    }
}
