package services.http.servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.managers.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    // Не знаю, как писать эти тесты по разным методам. Очень много кода, поэтому решил сделать большие тесты,
    // где проверяется все. Если можно как-то правильней, напишите)
    private final static URI ROOT = URI.create("http://localhost:8080/tasks");
    private static final URI TASK_URL = URI.create(ROOT + "/task");
    private static final URI EPIC_URL = URI.create(ROOT + "/epic");
    private static final URI SUBTASK_URL = URI.create(ROOT + "/subtask");
    private static final URI EPIC_SUBTASKS_URL = URI.create(ROOT + "/subtask/epic");
    private static HttpTaskServer taskServer;
    private static KVServer kvServer;
    private final Gson gson = Managers.getGson();
    private HttpClient client;

    @BeforeEach
    public void startSevers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void stopServers() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    public void shouldHandleOperationsWithTasks() throws IOException, InterruptedException {
        Task requestTask = new Task("0", "0", TaskStatus.NEW);
        requestTask.setId(1);
        String taskJson = gson.toJson(requestTask);

        HttpRequest postTaskRequest = createPostRequest(TASK_URL, taskJson);
        HttpRequest getTasksRequest = createGetRequest(TASK_URL);
        HttpRequest getTaskRequest = createGetByIdRequest(TASK_URL, 1);
        HttpRequest deleteTaskRequest = createDeleteByIdRequest(TASK_URL, 1);

        HttpResponse<Void> postResponse = client.send(postTaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> responseTasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, responseTasks.size(), "Неправильная длинна списка задач.");
        assertEquals(requestTask, responseTasks.get(0), "Задачи не совпадают.");

        getResponse = client.send(getTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Task responseTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(requestTask, responseTask, "Задачи не совпадают.");

        requestTask.setStartTime(LocalDateTime.now());
        requestTask.setDuration(60);
        taskJson = gson.toJson(requestTask);

        postTaskRequest = createPostRequest(TASK_URL, taskJson);
        postResponse = client.send(postTaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(requestTask, responseTask, "Задачи не совпадают.");

        HttpResponse<Void> deleteResponse = client.send(deleteTaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseTasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, responseTasks.size(), "Неправильная длинна списка задач.");
    }

    @Test
    public void shouldHandleOperationsWithEpicsAndSubtasks() throws IOException, InterruptedException {
        Epic requestEpic = new Epic("1", "1");
        requestEpic.setId(1);
        Subtask requestSubtask = new Subtask(1, "2", "2", TaskStatus.NEW);
        requestSubtask.setId(2);

        String epicJson = gson.toJson(requestEpic);
        String subtaskJson = gson.toJson(requestSubtask);

        HttpRequest postEpicRequest = createPostRequest(EPIC_URL, epicJson);
        HttpRequest getEpicsRequest = createGetRequest(EPIC_URL);
        HttpRequest getEpicRequest = createGetByIdRequest(EPIC_URL, 1);
        HttpRequest getEpicSubtasksRequest = createGetByIdRequest(EPIC_SUBTASKS_URL, 1);

        HttpRequest postSubtaskRequest = createPostRequest(SUBTASK_URL, subtaskJson);
        HttpRequest getSubtasksRequest = createGetRequest(SUBTASK_URL);
        HttpRequest getSubtaskRequest = createGetByIdRequest(SUBTASK_URL, 2);
        HttpRequest deleteSubtaskRequest = createDeleteByIdRequest(SUBTASK_URL, 2);

        HttpResponse<Void> postResponse = client.send(postEpicRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        requestEpic.addSubtaskID(2);

        HttpResponse<String> getResponse = client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Epic> responseEpics = gson.fromJson(getResponse.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(1, responseEpics.size(), "Неправильная длинна списка эпиков.");
        assertEquals(requestEpic, responseEpics.get(0), "Эпики не совпадают.");

        getResponse = client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Subtask> responseSubtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(1, responseSubtasks.size(), "Неправильная длинна списка подзадач.");
        assertEquals(requestSubtask, responseSubtasks.get(0), "Подзадачи не совпадают.");

        getResponse = client.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Epic responseEpic = gson.fromJson(getResponse.body(), Epic.class);
        assertEquals(requestEpic, responseEpic, "Эпики не совпадают.");

        getResponse = client.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Subtask responseSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertEquals(requestSubtask, responseSubtask, "Подзадачи не совпадают.");

        getResponse = client.send(getEpicSubtasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseSubtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(1, responseSubtasks.size(), "Неправильная длинна списка подзадач.");
        assertEquals(requestSubtask, responseSubtasks.get(0), "Подзадачи не совпадают.");

        requestEpic.setStartTime(LocalDateTime.now());
        epicJson = gson.toJson(requestEpic);

        postEpicRequest = createPostRequest(EPIC_URL, epicJson);
        postResponse = client.send(postEpicRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        requestSubtask.setStartTime(LocalDateTime.now().plusHours(2));
        requestSubtask.setDuration(60);
        requestEpic.setStartTime(requestSubtask.getStartTime());
        requestEpic.setEndTime(requestSubtask.getStartTime().plusMinutes(60));
        requestEpic.setDuration(requestSubtask.getDuration());
        subtaskJson = gson.toJson(requestSubtask);

        postSubtaskRequest = createPostRequest(SUBTASK_URL, subtaskJson);
        postResponse = client.send(postSubtaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseEpic = gson.fromJson(getResponse.body(), Epic.class);
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        assertEquals(requestEpic, responseEpic, "Эпики не совпадают.");

        getResponse = client.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        assertEquals(requestSubtask, responseSubtask, "Подзадачи не совпадают.");

        HttpResponse<Void> deleteResponse = client.send(deleteSubtaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseEpics = gson.fromJson(getResponse.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(0, responseEpics.size(), "Неправильная длинна списка эпиков.");

        getResponse = client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        responseSubtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(0, responseSubtasks.size(), "Неправильная длинна списка подзадач.");
    }

    @Test
    public void shouldHandleMultipleTypesTasksRequests() throws IOException, InterruptedException {
        Task task1 = new Task("1", "2", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("2", "2", TaskStatus.NEW);
        task2.setId(2);
        Epic epic3 = new Epic("3", "3");
        epic3.setId(3);
        Subtask subtask4 = new Subtask(3, "4", "4", TaskStatus.NEW);
        subtask4.setId(4);
        Subtask subtask5 = new Subtask(3, "5", "5", TaskStatus.NEW);
        subtask5.setId(5);
        Epic epic5 = new Epic("6", "6");
        epic5.setId(6);
        Subtask subtask7 = new Subtask(6, "7", "7", TaskStatus.NEW);
        subtask7.setId(7);

        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);
        String jsonEpic3 = gson.toJson(epic3);
        String jsonSubtask4 = gson.toJson(subtask4);
        String jsonSubtask5 = gson.toJson(subtask5);
        String jsonEpic6 = gson.toJson(epic5);
        String jsonSubtask7 = gson.toJson(subtask7);

        HttpRequest postTask0Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask2);
        HttpRequest postEpic2Request = createPostRequest(EPIC_URL, jsonEpic3);
        HttpRequest postSubtask3Request = createPostRequest(SUBTASK_URL, jsonSubtask4);
        HttpRequest postSubtask4Request = createPostRequest(SUBTASK_URL, jsonSubtask5);
        HttpRequest postEpic5Request = createPostRequest(EPIC_URL, jsonEpic6);
        HttpRequest postSubtask6Request = createPostRequest(SUBTASK_URL, jsonSubtask7);

        HttpRequest getEpic6Request = createGetByIdRequest(EPIC_URL, 6);
        HttpRequest getSubtask7Request = createGetByIdRequest(SUBTASK_URL, 7);
        HttpRequest getTasksRequest = createGetRequest(TASK_URL);
        HttpRequest getSubtasksRequest = createGetRequest(SUBTASK_URL);
        HttpRequest getEpicsRequest = createGetRequest(EPIC_URL);

        HttpRequest deleteSubtask7Request = createDeleteByIdRequest(SUBTASK_URL, 7);
        HttpRequest deleteAllRequest = createDeleteRequest(ROOT);

        HttpResponse<Void> postResponse = client.send(postTask0Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postEpic2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask3Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask4Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postEpic5Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask6Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getAllRequest = createGetRequest(ROOT);

        HttpResponse<String> getResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> prioritizedTasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(5, prioritizedTasks.size(), "Неправильная длинна списка задач.");
        assertEquals(task1, prioritizedTasks.get(0), "Задачи не совпадают.");
        assertEquals(task2, prioritizedTasks.get(1), "Задачи не совпадают.");
        assertEquals(new Task(subtask4), prioritizedTasks.get(2), "Задачи не совпадают.");
        assertEquals(new Task(subtask5), prioritizedTasks.get(3), "Задачи не совпадают.");
        assertEquals(new Task(subtask7), prioritizedTasks.get(4), "Задачи не совпадают.");

        HttpResponse<Void> deleteResponse = client.send(deleteSubtask7Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getSubtask7Request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(getEpic6Request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        prioritizedTasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(4, prioritizedTasks.size(), "Неправильная длинна списка задач.");
        assertEquals(task1, prioritizedTasks.get(0), "Задачи не совпадают.");
        assertEquals(task2, prioritizedTasks.get(1), "Задачи не совпадают.");
        assertEquals(new Task(subtask4), prioritizedTasks.get(2), "Задачи не совпадают.");
        assertEquals(new Task(subtask5), prioritizedTasks.get(3), "Задачи не совпадают.");

        deleteResponse = client.send(deleteAllRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> tasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, tasks.size(), "Неправильная длинна списка задач.");

        getResponse = client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Epic> epics = gson.fromJson(getResponse.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(0, epics.size(), "Неправильная длинна списка задач.");

        getResponse = client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Subtask> subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(0, subtasks.size(), "Неправильная длинна списка задач.");
    }

    private HttpRequest createPostRequest(URI uri, String json) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private HttpRequest createDeleteByIdRequest(URI uri, int id) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri + "?id=" + id))
                .DELETE()
                .build();
    }

    private HttpRequest createGetByIdRequest(URI uri, int id) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri + "?id=" + id))
                .GET()
                .build();
    }

    private HttpRequest createGetRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).GET().build();
    }

    private HttpRequest createDeleteRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).DELETE().build();
    }
}