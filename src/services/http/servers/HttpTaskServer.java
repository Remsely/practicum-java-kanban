package services.http.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import services.managers.tasks.TaskManager;
import services.managers.util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager manager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }

    private void handler(HttpExchange h) {
        try {
            System.out.println("\n/tasks: " + h.getRequestURI()); // почему-то при POST данная строка не печатается
            final String path = h.getRequestURI().getPath().substring("/tasks".length());

            switch (path) {
                case "":
                case "/":
                    handleRoot(h);
                    break;
                case "/task":
                    handleTask(h);
                    break;
                case "/subtask":
                    handleSubtask(h);
                    break;
                case "/subtask/epic":
                    handleSubtaskEpic(h);
                    break;
                case "/epic":
                    handleEpic(h);
                    break;
                case "/history":
                    handleHistory(h);
                    break;
                default:
                    System.out.println("Неизвестный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(404, 0);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleRoot(HttpExchange h) throws IOException {
        switch (h.getRequestMethod()) {
            case "GET":
                String response = gson.toJson(manager.getPrioritizedTasks());
                sendText(h, response);
                break;
            case "DELETE":
                manager.clear();
                h.sendResponseHeaders(200, 0);
                break;
            default:
                System.out.println("/tasks ждет GET или DELETE-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                handleGetTask(h, query);
                break;
            case "POST":
                handlePostTask(h);
                break;
            case "DELETE":
                handleDeleteTask(h, query);
                break;
            default:
                System.out.println("Неизвестный запрос для /task: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleSubtask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                handleGetSubtask(h, query);
                break;
            case "POST":
                handlePostSubtask(h);
                break;
            case "DELETE":
                handleDeleteTask(h, query);
                break;
            default:
                System.out.println("Неизвестный запрос для /task: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleSubtaskEpic(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();

        if (query == null) {
            System.out.println(h.getRequestURI().getPath() + " ждет query, но не получил его");
            h.sendResponseHeaders(400, 0);
            return;
        }

        int id = getQueryId(query);
        List<Subtask> subtasks = manager.getEpicSubtasks(id);

        if (subtasks != null) {
            String response = gson.toJson(subtasks);
            sendText(h, response);
        } else {
            System.out.println("Эпика с индексом " + id + "не существует!");
            h.sendResponseHeaders(404, 0);
        }
    }

    private void handleEpic(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                handleGetEpic(h, query);
                break;
            case "POST":
                handlePostEpic(h);
                break;
            case "DELETE":
                handleDeleteTask(h, query);
                break;
            default:
                System.out.println("Неизвестный запрос для /task: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleHistory(HttpExchange h) throws IOException {
        if ("GET".equals(h.getRequestMethod())) {
            String response = gson.toJson(manager.getHistory());
            sendText(h, response);
            return;
        }
        System.out.println("/history ждет GET-запрос, а получил " + h.getRequestMethod());
        h.sendResponseHeaders(405, 0);
    }

    private void handleGetTask(HttpExchange h, String query) throws IOException {
        if (query == null) {
            List<Task> tasks = manager.getTasks();
            String response = gson.toJson(tasks);
            sendText(h, response);
            return;
        }

        int id = getQueryId(query);
        Task task = manager.getTask(id);

        if (task != null) {
            String response = gson.toJson(task);
            sendText(h, response);
        } else {
            System.out.println("Задачи с индексом " + id + "не существует!");
            h.sendResponseHeaders(404, 0);
        }
    }

    private void handlePostTask(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), UTF_8);
        Task task = gson.fromJson(body, Task.class);

        int taskId = task.getId();
        if (manager.getTask(taskId) != null)
            manager.update(taskId, task);
        else
            manager.add(task);

        h.sendResponseHeaders(200, 0);
    }

    private void handleDeleteTask(HttpExchange h, String query) throws IOException {
        if (query == null) {
            System.out.println(h.getRequestURI().getPath() + " ждет query, но не получил его");
            h.sendResponseHeaders(400, 0);
            return;
        }

        int id = getQueryId(query);
        if (manager.remove(id))
            h.sendResponseHeaders(200, 0);
        else {
            System.out.println("Задачи с индексом " + id + "не существует!");
            h.sendResponseHeaders(404, 0);
        }
    }

    private void handleGetSubtask(HttpExchange h, String query) throws IOException {
        if (query == null) {
            List<Subtask> subtasks = manager.getSubtasks();
            String response = gson.toJson(subtasks);
            sendText(h, response);
            return;
        }

        int id = getQueryId(query);
        Subtask subtask = manager.getSubtask(id);

        if (subtask != null) {
            String response = gson.toJson(subtask);
            sendText(h, response);
        } else {
            System.out.println("Подзадачи с индексом " + id + "не существует!");
            h.sendResponseHeaders(404, 0);
        }
    }

    private void handlePostSubtask(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        int taskId = subtask.getId();
        if (manager.getSubtask(taskId) != null)
            manager.update(taskId, subtask);
        else
            manager.add(subtask);

        h.sendResponseHeaders(200, 0);
    }

    private void handleGetEpic(HttpExchange h, String query) throws IOException {
        if (query == null) {
            List<Epic> epics = manager.getEpics();
            String response = gson.toJson(epics);
            sendText(h, response);
            return;
        }

        int id = getQueryId(query);
        Epic epic = manager.getEpic(id);

        if (epic != null) {
            String response = gson.toJson(epic);
            sendText(h, response);
        } else {
            System.out.println("Эпика с индексом " + id + "не существует!");
            h.sendResponseHeaders(404, 0);
        }
    }

    private void handlePostEpic(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        int taskId = epic.getId();
        if (manager.getEpic(taskId) != null)
            manager.update(taskId, epic);
        else
            manager.add(epic);

        h.sendResponseHeaders(200, 0);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private int getQueryId(String query) {
        return Integer.parseInt(query.substring("id=".length()));
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Отключение сервера на порту " + PORT + "...");
        server.stop(1);
        System.out.println("Сервер отключен.");
    }
}