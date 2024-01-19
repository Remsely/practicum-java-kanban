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
import services.managers.tasks.InMemoryTaskManager;
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
    private final static URI ROOT = URI.create("http://localhost:8080/tasks");
    private static final URI TASK_URL = URI.create(ROOT + "/task");
    private static final URI EPIC_URL = URI.create(ROOT + "/epic");
    private static final URI SUBTASK_URL = URI.create(ROOT + "/subtask");
    private static final URI EPIC_SUBTASKS_URL = URI.create(ROOT + "/subtask/epic");
    private static final URI HISTORY_URL = URI.create(ROOT + "/history");
    private static HttpTaskServer taskServer;
    private final Gson gson = Managers.getGson();
    private HttpClient client;

    @BeforeEach
    public void startSevers() throws IOException {
        taskServer = new HttpTaskServer(new InMemoryTaskManager());
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void stopServers() {
        taskServer.stop();
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("2", "2", TaskStatus.NEW);
        task2.setId(2);
        Epic epic3 = new Epic("3", "3");
        epic3.setId(3);
        Subtask subtask4 = new Subtask(3, "4", "4", TaskStatus.NEW);
        subtask4.setId(4);

        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);
        String jsonEpic3 = gson.toJson(epic3);
        String jsonSubtask4 = gson.toJson(subtask4);

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest postTask2Request = createPostRequest(TASK_URL, jsonTask2);
        HttpRequest postEpic3Request = createPostRequest(EPIC_URL, jsonEpic3);
        HttpRequest postSubtask4Request = createPostRequest(SUBTASK_URL, jsonSubtask4);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postEpic3Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask4Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getPrioritizedTasksRequests = createGetRequest(ROOT);

        HttpResponse<String> getResponse = client.send(
                getPrioritizedTasksRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> tasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(3, tasks.size(), "Неправильная длинна списка задач.");
        assertEquals(task1, tasks.get(0), "Ожидается другая задача по этому индексу списка.");
        assertEquals(task2, tasks.get(1), "Ожидается другая задача по этому индексу списка.");
        assertEquals(new Task(subtask4), tasks.get(2), "Ожидается другая задача по этому индексу списка.");
    }

    @Test
    public void shouldDeleteAll() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("2", "2", TaskStatus.NEW);
        task2.setId(2);
        Epic epic3 = new Epic("3", "3");
        epic3.setId(3);
        Subtask subtask4 = new Subtask(3, "4", "4", TaskStatus.NEW);
        subtask4.setId(4);

        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);
        String jsonEpic3 = gson.toJson(epic3);
        String jsonSubtask4 = gson.toJson(subtask4);

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest postTask2Request = createPostRequest(TASK_URL, jsonTask2);
        HttpRequest postEpic3Request = createPostRequest(EPIC_URL, jsonEpic3);
        HttpRequest postSubtask4Request = createPostRequest(SUBTASK_URL, jsonSubtask4);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postEpic3Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask4Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getPrioritizedTasksRequests = createGetRequest(ROOT);
        HttpRequest deleteAllRequest = createDeleteRequest(ROOT);

        HttpResponse<String> getResponse = client.send(
                getPrioritizedTasksRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> tasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(3, tasks.size(), "Неправильная длинна списка задач.");

        HttpResponse<Void> deleteResponse = client.send(
                deleteAllRequest,
                HttpResponse.BodyHandlers.discarding()
        );
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getPrioritizedTasksRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        tasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, tasks.size(), "Неправильная длинна списка задач.");
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("2", "2", TaskStatus.NEW);
        task2.setId(2);

        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);

        HttpRequest getTasksRequests = createGetRequest(TASK_URL);

        HttpResponse<String> getResponse = client.send(
                getTasksRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> tasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, tasks.size(), "Неправильная длинна списка задач.");

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest postTask2Request = createPostRequest(TASK_URL, jsonTask2);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getTasksRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        tasks = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasks.size(), "Неправильная длинна списка задач.");
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);
        Subtask subtask2 = new Subtask(1,"2", "2", TaskStatus.NEW);
        subtask2.setId(2);
        Subtask subtask3 = new Subtask(1,"3", "3", TaskStatus.NEW);
        subtask3.setId(3);

        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask2 = gson.toJson(subtask2);
        String jsonSubtask3 = gson.toJson(subtask3);

        HttpRequest getSubtasksRequest = createGetRequest(SUBTASK_URL);

        HttpResponse<String> getResponse = client.send(
                getSubtasksRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Subtask> subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(0, subtasks.size(), "Неправильная длинна списка подзадач.");

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest postSubtask2Request = createPostRequest(SUBTASK_URL, jsonSubtask2);
        HttpRequest postSubtask3Request = createPostRequest(SUBTASK_URL, jsonSubtask3);

        HttpResponse<Void> postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask3Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getSubtasksRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(2, subtasks.size(), "Неправильная длинна списка подзадач.");
    }

    @Test
    public void shouldGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);
        Epic epic2 = new Epic("2", "2");
        epic2.setId(2);

        String jsonTask1 = gson.toJson(epic1);
        String jsonTask2 = gson.toJson(epic2);

        HttpRequest getEpicsRequests = createGetRequest(EPIC_URL);

        HttpResponse<String> getResponse = client.send(
                getEpicsRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Epic> epics = gson.fromJson(getResponse.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(0, epics.size(), "Неправильная длинна списка эпиков.");

        HttpRequest postTask1Request = createPostRequest(EPIC_URL, jsonTask1);
        HttpRequest postTask2Request = createPostRequest(EPIC_URL, jsonTask2);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getEpicsRequests,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        epics = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, epics.size(), "Неправильная длинна списка эпиков.");
    }

    @Test
    public void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);
        Epic epic2 = new Epic("2", "2");
        epic2.setId(2);
        Subtask subtask3 = new Subtask(1,"3", "3", TaskStatus.NEW);
        subtask3.setId(3);
        Subtask subtask4 = new Subtask(1,"4", "4", TaskStatus.NEW);
        subtask4.setId(4);
        Subtask subtask5 = new Subtask(2,"5", "5", TaskStatus.NEW);
        subtask5.setId(5);

        String jsonEpic1 = gson.toJson(epic1);
        String jsonEpic2 = gson.toJson(epic2);
        String jsonSubtask3 = gson.toJson(subtask3);
        String jsonSubtask4 = gson.toJson(subtask4);
        String jsonSubtask5 = gson.toJson(subtask5);

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest postEpic2Request = createPostRequest(EPIC_URL, jsonEpic2);
        HttpRequest postSubtask3Request = createPostRequest(SUBTASK_URL, jsonSubtask3);
        HttpRequest postSubtask4Request = createPostRequest(SUBTASK_URL, jsonSubtask4);
        HttpRequest postSubtask5Request = createPostRequest(SUBTASK_URL, jsonSubtask5);

        HttpResponse<Void> postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postEpic2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getEpic1Subtasks = createGetRequest(EPIC_SUBTASKS_URL, 1);
        HttpRequest getEpic2Subtasks = createGetRequest(EPIC_SUBTASKS_URL, 2);

        epic1.addSubtaskID(3);
        epic1.addSubtaskID(4);
        epic2.addSubtaskID(5);

        HttpResponse<String> getResponse = client.send(
                getEpic1Subtasks,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Subtask> subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(0, subtasks.size(), "Неправильная длинна списка эпиков.");

        getResponse = client.send(
                getEpic2Subtasks,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(0, subtasks.size(), "Неправильная длинна списка эпиков.");

        postResponse = client.send(postSubtask3Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask4Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postSubtask5Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getEpic1Subtasks,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(2, subtasks.size(), "Неправильная длинна списка эпиков.");

        getResponse = client.send(
                getEpic2Subtasks,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        subtasks = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(1, subtasks.size(), "Неправильная длинна списка эпиков.");

        HttpRequest incorrectGetResponse = createGetRequest(EPIC_SUBTASKS_URL, 111);

        getResponse = client.send(
                incorrectGetResponse,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("2", "2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("3", "3", TaskStatus.NEW);
        task3.setId(3);
        Task task4 = new Task("4", "4", TaskStatus.NEW);
        task4.setId(4);
        Task task5 = new Task("5", "5", TaskStatus.NEW);
        task5.setId(5);

        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);
        String jsonTask3 = gson.toJson(task3);
        String jsonTask4 = gson.toJson(task4);
        String jsonTask5 = gson.toJson(task5);

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest postTask2Request = createPostRequest(TASK_URL, jsonTask2);
        HttpRequest postTask3Request = createPostRequest(TASK_URL, jsonTask3);
        HttpRequest postTask4Request = createPostRequest(TASK_URL, jsonTask4);
        HttpRequest postTask5Request = createPostRequest(TASK_URL, jsonTask5);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask3Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask4Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        postResponse = client.send(postTask5Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getTask1Request = createGetRequest(TASK_URL, 1);
        HttpRequest getTask2Request = createGetRequest(TASK_URL, 2);
        HttpRequest getTask3Request = createGetRequest(TASK_URL, 3);
        HttpRequest getTask4Request = createGetRequest(TASK_URL, 4);
        HttpRequest getTask5Request = createGetRequest(TASK_URL, 5);

        HttpRequest getHistoryRequest = createGetRequest(HISTORY_URL);

        HttpResponse<String> getResponse = client.send(
                getHistoryRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        List<Task> history = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, history.size(), "Неправильная длинна списка истории.");

        getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(
                getTask4Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(
                getTask3Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(
                getTask5Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(
                getTask2Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getHistoryRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        history = gson.fromJson(getResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(5, history.size(), "Неправильная длинна списка истории.");
        assertEquals(task4, history.get(0), "Ожидается другая задача по этому индексу списка.");
        assertEquals(task3, history.get(1), "Ожидается другая задача по этому индексу списка.");
        assertEquals(task5, history.get(2), "Ожидается другая задача по этому индексу списка.");
        assertEquals(task2, history.get(3), "Ожидается другая задача по этому индексу списка.");
        assertEquals(task1, history.get(4), "Ожидается другая задача по этому индексу списка.");
    }

    @Test
    public void shouldPostTask() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);

        String jsonTask1 = gson.toJson(task1);

        HttpRequest emptyPostRequest = createPostRequest(TASK_URL, "");
        HttpResponse<Void> postResponse = client.send(emptyPostRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest getTask1Request = createGetRequest(TASK_URL, 1);

        postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Task gottenTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(task1, gottenTask, "Задачи не совпадают.");

        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(60);
        jsonTask1 = gson.toJson(task1);
        postTask1Request = createPostRequest(TASK_URL, jsonTask1);

        postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        gottenTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(task1, gottenTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldPostEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);

        String jsonEpic1 = gson.toJson(epic1);

        HttpRequest emptyPostRequest = createPostRequest(EPIC_URL, "");
        HttpResponse<Void> postResponse = client.send(emptyPostRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest getEpic1Request = createGetRequest(EPIC_URL, 1);

        postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getEpic1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Epic gottenEpic = gson.fromJson(getResponse.body(), Epic.class);
        assertEquals(epic1, gottenEpic, "Эпики не совпадают.");

        Epic epic2 = new Epic("2", "2");
        epic2.setId(1);
        String jsonEpic2 = gson.toJson(epic2);
        HttpRequest postEpic2Request = createPostRequest(EPIC_URL, jsonEpic2);

        postResponse = client.send(postEpic2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(405, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldPostSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);
        Subtask subtask2 = new Subtask(1, "2", "2", TaskStatus.NEW);
        subtask2.setId(2);

        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask2 = gson.toJson(subtask2);

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest postSubtask2Request = createPostRequest(SUBTASK_URL, jsonSubtask2);
        HttpRequest emptyPostRequest = createPostRequest(SUBTASK_URL, "");

        HttpResponse<Void> postResponse = client.send(postSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        postResponse = client.send(emptyPostRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(400, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getSubtask2Request = createGetRequest(SUBTASK_URL, 2);

        postResponse = client.send(postSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getSubtask2Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Subtask gottenSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertEquals(subtask2, gottenSubtask, "Задачи не совпадают.");

        subtask2.setStartTime(LocalDateTime.now());
        subtask2.setDuration(60);
        jsonSubtask2 = gson.toJson(subtask2);
        postSubtask2Request = createPostRequest(SUBTASK_URL, jsonSubtask2);

        postResponse = client.send(postSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getSubtask2Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        gottenSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertEquals(subtask2, gottenSubtask, "Подзадачи не совпадают.");
    }

    @Test
    public void shouldGetTask() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);

        String jsonTask1 = gson.toJson(task1);

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest getTask1Request = createGetRequest(TASK_URL, 1);
        HttpRequest incorrectGetRequest = createGetRequest(TASK_URL, 111);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Task gottenTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(task1, gottenTask, "Задачи не совпадают.");

        getResponse = client.send(
                incorrectGetRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldGetEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);

        String jsonTask1 = gson.toJson(epic1);

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonTask1);
        HttpRequest getEpic1Request = createGetRequest(EPIC_URL, 1);
        HttpRequest incorrectGetRequest = createGetRequest(EPIC_URL, 111);

        HttpResponse<Void> postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getEpic1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Epic gottenEpic = gson.fromJson(getResponse.body(), Epic.class);
        assertEquals(epic1, gottenEpic, "Эпики не совпадают.");

        getResponse = client.send(
                incorrectGetRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldGetSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);
        Subtask subtask2 = new Subtask(1, "2", "2", TaskStatus.NEW);
        subtask2.setId(2);

        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask2 = gson.toJson(subtask2);

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest postSubtask2Request = createPostRequest(SUBTASK_URL, jsonSubtask2);

        HttpResponse<Void> postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        postResponse = client.send(postSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest incorrectGetRequest = createGetRequest(SUBTASK_URL, 111);
        HttpRequest getSubtask2Request = createGetRequest(SUBTASK_URL, 2);

        HttpResponse<String> getResponse = client.send(
                getSubtask2Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
        Subtask gottenSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertEquals(subtask2, gottenSubtask, "Подзадачи не совпадают.");

        getResponse = client.send(
                incorrectGetRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        task1.setId(1);

        String jsonTask1 = gson.toJson(task1);

        HttpRequest postTask1Request = createPostRequest(TASK_URL, jsonTask1);
        HttpRequest getTask1Request = createGetRequest(TASK_URL, 1);
        HttpRequest deleteTask1Request = createDeleteRequest(TASK_URL, 1);
        HttpRequest incorrectDeleteRequest = createDeleteRequest(TASK_URL, 111);

        HttpResponse<Void> postResponse = client.send(postTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<Void> deleteResponse = client.send(deleteTask1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getTask1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                incorrectDeleteRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);

        String jsonEpic1 = gson.toJson(epic1);

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest getEpic1Request = createGetRequest(EPIC_URL, 1);
        HttpRequest deleteEpic1Request = createDeleteRequest(EPIC_URL, 1);
        HttpRequest incorrectDeleteRequest = createDeleteRequest(EPIC_URL, 111);

        HttpResponse<Void> postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<String> getResponse = client.send(
                getEpic1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<Void> deleteResponse = client.send(deleteEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getEpic1Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                incorrectDeleteRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("1", "1");
        epic1.setId(1);
        Subtask subtask2 = new Subtask(1, "2", "2", TaskStatus.NEW);
        subtask2.setId(2);

        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask2 = gson.toJson(subtask2);

        HttpRequest postEpic1Request = createPostRequest(EPIC_URL, jsonEpic1);
        HttpRequest postSubtask2Request = createPostRequest(SUBTASK_URL, jsonSubtask2);

        HttpResponse<Void> postResponse = client.send(postEpic1Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        postResponse = client.send(postSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, postResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpRequest getSubtask2Request = createGetRequest(SUBTASK_URL, 2);
        HttpRequest deleteSubtask2Request = createDeleteRequest(SUBTASK_URL, 2);
        HttpRequest incorrectDeleteRequest = createDeleteRequest(SUBTASK_URL, 111);

        HttpResponse<String> getResponse = client.send(
                getSubtask2Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        HttpResponse<Void> deleteResponse = client.send(deleteSubtask2Request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, deleteResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                getSubtask2Request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");

        getResponse = client.send(
                incorrectDeleteRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, getResponse.statusCode(), "Сервер возвращает неправильный код ответа.");
    }

    private HttpRequest createPostRequest(URI uri, String json) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private HttpRequest createDeleteRequest(URI uri, int id) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri + "?id=" + id))
                .DELETE()
                .build();
    }

    private HttpRequest createGetRequest(URI uri, int id) {
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